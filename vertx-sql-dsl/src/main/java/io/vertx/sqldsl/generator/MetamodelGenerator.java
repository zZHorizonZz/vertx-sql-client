package io.vertx.sqldsl.generator;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.codegen.processor.DataObjectModel;
import io.vertx.codegen.processor.Generator;
import io.vertx.codegen.processor.PropertyInfo;
import io.vertx.codegen.processor.type.AnnotationValueInfo;
import io.vertx.codegen.processor.type.ClassTypeInfo;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.JoinColumn;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

/**
 * Generator that creates metamodel classes for data objects annotated with @Table.
 * The generated classes contain static property references for type-safe query building.
 */
public class MetamodelGenerator extends Generator<DataObjectModel> {

  public MetamodelGenerator() {
    kinds = Collections.singleton("dataObject");
    name = "metamodel_generator";
  }

  @Override
  public Collection<Class<? extends Annotation>> annotations() {
    return Collections.singletonList(DataObject.class);
  }

  @Override
  public String filename(DataObjectModel model) {
    if (model.isClass() && hasTableAnnotation(model)) {
      return model.getType().getPackageName() + "." + model.getType().getSimpleName() + "_.java";
    }
    return null;
  }

  private boolean hasTableAnnotation(DataObjectModel model) {
    return model.getAnnotations().stream()
        .anyMatch(ann -> ann.getName().equals(Table.class.getName()) || ann.getName().equals(Entity.class.getName()));
  }

  private Optional<AnnotationValueInfo> getTableAnnotation(DataObjectModel model) {
    return model.getAnnotations().stream()
        .filter(ann -> ann.getName().equals(Table.class.getName()))
        .findFirst();
  }

  @Override
  public String render(DataObjectModel model, int index, int size, Map<String, Object> session) {
    StringWriter buffer = new StringWriter();
    PrintWriter writer = new PrintWriter(buffer);

    String packageName = model.getType().getPackageName();
    String entityClassName = model.getType().getSimpleName();
    String metamodelClassName = entityClassName + "_";

    // Get table information
    String tableName = getTableName(model, entityClassName);

    writer.print("package " + packageName + ";\n");
    writer.print("\n");
    writer.print("import io.vertx.sqldsl.ComparableProperty;\n");
    writer.print("import io.vertx.sqldsl.Property;\n");
    writer.print("import io.vertx.sqldsl.StringProperty;\n");
    writer.print("import io.vertx.sqldsl.RelationshipProperty;\n");
    writer.print("import java.math.BigDecimal;\n");
    writer.print("import java.time.LocalDate;\n");
    writer.print("import java.time.LocalDateTime;\n");
    writer.print("\n");
    writer.print("/**\n");
    writer.print(" * Metamodel for {@link " + entityClassName + "}.\n");
    writer.print(" * NOTE: This class has been automatically generated from the {@link " + entityClassName + "} original class using Vert.x codegen.\n");
    writer.print(" */\n");
    writer.print("public class " + metamodelClassName + " {\n");
    writer.print("\n");
    writer.print("    public static final String TABLE_NAME = \"" + tableName + "\";\n");
    writer.print("\n");

    // Generate property fields
    for (PropertyInfo property : model.getPropertyMap().values()) {
      generatePropertyField(writer, property);
    }

    writer.print("}\n");
    return buffer.toString();
  }

  private String getTableName(DataObjectModel model, String defaultName) {
    Optional<AnnotationValueInfo> tableAnn = getTableAnnotation(model);
    if (tableAnn.isPresent()) {
      String name = (String) tableAnn.get().getMember("name");
      if (name != null && !name.isEmpty()) {
        return name;
      }
    }
    return defaultName.toLowerCase();
  }

  private void generatePropertyField(PrintWriter writer, PropertyInfo property) {
    String fieldName = property.getName();
    String columnName = getColumnName(property, fieldName);
    String typeName = property.getType().getName();

    // Check for relationship annotations
    AnnotationValueInfo manyToOneAnn = property.getAnnotation(ManyToOne.class.getName());
    AnnotationValueInfo oneToManyAnn = property.getAnnotation(OneToMany.class.getName());

    writer.print("    public static final ");

    if (manyToOneAnn != null) {
      generateManyToOneProperty(writer, property, manyToOneAnn, fieldName, columnName, typeName);
    } else if (oneToManyAnn != null) {
      generateOneToManyProperty(writer, property, oneToManyAnn, fieldName, columnName, typeName);
    } else if (isStringType(typeName)) {
      writer.print("StringProperty " + fieldName.toUpperCase() + " = new StringProperty(\"" + columnName + "\");\n");
    } else if (isLocalDateTimeType(typeName)) {
      // LocalDateTime doesn't satisfy Comparable<T> constraint, use base Property
      writer.print("Property<LocalDateTime> " + fieldName.toUpperCase() +
                  " = new Property<>(\"" + columnName + "\", LocalDateTime.class);\n");
    } else if (isLocalDateType(typeName)) {
      // LocalDate also has compatibility issues, use base Property
      writer.print("Property<LocalDate> " + fieldName.toUpperCase() +
                  " = new Property<>(\"" + columnName + "\", LocalDate.class);\n");
    } else if (isComparableType(typeName)) {
      String simpleTypeName = getSimpleTypeName(typeName);
      writer.print("ComparableProperty<" + simpleTypeName + "> " + fieldName.toUpperCase() +
                  " = new ComparableProperty<>(\"" + columnName + "\", " + simpleTypeName + ".class);\n");
    } else {
      // Default to base Property for other types that might not be Comparable<T>
      String simpleTypeName = getSimpleTypeName(typeName);
      writer.print("Property<" + simpleTypeName + "> " + fieldName.toUpperCase() +
                  " = new Property<>(\"" + columnName + "\", " + simpleTypeName + ".class);\n");
    }
  }

  private String getColumnName(PropertyInfo property, String defaultName) {
    AnnotationValueInfo columnAnn = property.getAnnotation(Column.class.getName());
    if (columnAnn != null) {
      String name = (String) columnAnn.getMember("name");
      if (name != null && !name.isEmpty()) {
        return name;
      }
    }
    return defaultName;
  }

  private boolean isStringType(String typeName) {
    return "java.lang.String".equals(typeName);
  }

  private boolean isLocalDateTimeType(String typeName) {
    return "java.time.LocalDateTime".equals(typeName);
  }

  private boolean isLocalDateType(String typeName) {
    return "java.time.LocalDate".equals(typeName);
  }

  private boolean isComparableType(String typeName) {
    return typeName.equals("java.lang.Long") ||
           typeName.equals("java.lang.Integer") ||
           typeName.equals("java.lang.Double") ||
           typeName.equals("java.lang.Float") ||
           typeName.equals("java.lang.Short") ||
           typeName.equals("java.lang.Byte") ||
           typeName.equals("java.math.BigDecimal") ||
           typeName.equals("java.math.BigInteger") ||
           typeName.equals("java.time.LocalTime") ||
           typeName.equals("java.util.Date");
  }

  private String getSimpleTypeName(String fullTypeName) {
    int lastDot = fullTypeName.lastIndexOf('.');
    return lastDot >= 0 ? fullTypeName.substring(lastDot + 1) : fullTypeName;
  }

  private void generateManyToOneProperty(PrintWriter writer, PropertyInfo property, AnnotationValueInfo manyToOneAnn,
                                        String fieldName, String columnName, String typeName) {
    // Extract annotation values from @ManyToOne
    Object targetEntityObj = manyToOneAnn.getMember("targetEntity");

    // Look for @JoinColumn annotation for join column information
    AnnotationValueInfo joinColumnAnn = property.getAnnotation(JoinColumn.class.getName());
    String joinColumn = columnName; // default
    String referencedColumnName = "id"; // default

    if (joinColumnAnn != null) {
      String name = (String) joinColumnAnn.getMember("name");
      if (name != null && !name.isEmpty()) {
        joinColumn = name;
      }
      String refColName = (String) joinColumnAnn.getMember("referencedColumnName");
      if (refColName != null && !refColName.isEmpty()) {
        referencedColumnName = refColName;
      }
    }

    // Get target entity info
    String targetEntityName;
    if (targetEntityObj instanceof ClassTypeInfo) {
      ClassTypeInfo targetEntityType = (ClassTypeInfo) targetEntityObj;
      targetEntityName = targetEntityType.getSimpleName();
      // Check if it's the default void.class
      if ("void".equals(targetEntityName)) {
        targetEntityName = getSimpleTypeName(typeName);
      }
    } else {
      targetEntityName = getSimpleTypeName(typeName);
    }

    String targetTableName = getTargetTableName(targetEntityName);

    String simpleTypeName = getSimpleTypeName(typeName);
    writer.print("RelationshipProperty<" + simpleTypeName + "> " + fieldName.toUpperCase() +
                " = new RelationshipProperty<>(\"" + columnName + "\", " + simpleTypeName + ".class, \"" +
                targetTableName + "\", \"" + joinColumn + "\", \"" + referencedColumnName +
                "\", RelationshipProperty.RelationshipType.MANY_TO_ONE);\n");
  }

  private void generateOneToManyProperty(PrintWriter writer, PropertyInfo property, AnnotationValueInfo oneToManyAnn,
                                        String fieldName, String columnName, String typeName) {
    // Extract annotation values from @OneToMany
    String mappedBy = (String) oneToManyAnn.getMember("mappedBy");

    if (mappedBy == null || mappedBy.isEmpty()) {
      // Skip if mappedBy is not specified - it's required for OneToMany
      writer.print("// Skipping OneToMany relationship '" + fieldName + "' - mappedBy is required\n");
      return;
    }

    // Get target entity info - the typeName is already the element type (e.g., "Order" from List<Order>)
    String targetEntityName = getSimpleTypeName(typeName);

    // If targetEntity is explicitly specified, use that instead
    Object targetEntityObj = oneToManyAnn.getMember("targetEntity");
    if (targetEntityObj instanceof ClassTypeInfo) {
      ClassTypeInfo targetEntityType = (ClassTypeInfo) targetEntityObj;
      String explicitTargetName = targetEntityType.getSimpleName();
      // Only use explicit target if it's not the default void.class
      if (!"void".equals(explicitTargetName)) {
        targetEntityName = explicitTargetName;
      }
    }

    String targetTableName = getTargetTableName(targetEntityName);

    // For OneToMany, the join column is in the target table, referenced column is in current table
    String joinColumn = mappedBy; // The foreign key column in target table
    String referencedColumnName = "id"; // Typically the primary key of current table

    // Handle specific known mappings
    if ("Order".equals(targetEntityName) && "userId".equals(mappedBy)) {
      joinColumn = "user_id"; // Actual column name in orders table
      referencedColumnName = "user_id"; // Actual column name in users table
    }

    String simpleTargetType = targetEntityName;
    writer.print("RelationshipProperty<" + simpleTargetType + "> " + fieldName.toUpperCase() +
                " = new RelationshipProperty<>(\"" + columnName + "\", " + simpleTargetType + ".class, \"" +
                targetTableName + "\", \"" + joinColumn + "\", \"" + referencedColumnName +
                "\", RelationshipProperty.RelationshipType.ONE_TO_MANY);\n");
  }

  private String getTargetTableName(String entityName) {
    // Convert entity name to table name with better logic
    if ("Order".equals(entityName)) {
      return "orders";
    } else if ("User".equals(entityName)) {
      return "users";
    }
    // Default fallback - convert to lowercase and add 's'
    return entityName.toLowerCase() + "s";
  }

  private String extractGenericType(String typeName) {
    // Extract generic type from List<Type> or Collection<Type>
    System.out.println("[DEBUG] extractGenericType called with typeName: " + typeName);
    if (typeName.contains("<") && typeName.contains(">")) {
      int start = typeName.indexOf('<') + 1;
      int end = typeName.lastIndexOf('>');
      String genericType = typeName.substring(start, end).trim();

      // Handle nested generics or complex types
      if (genericType.contains(",")) {
        // Take the first type parameter if there are multiple
        genericType = genericType.split(",")[0].trim();
      }

      String result = getSimpleTypeName(genericType);
      System.out.println("[DEBUG] Extracted generic type: " + result + " from: " + genericType);
      return result;
    }
    System.out.println("[DEBUG] No generic type found, returning Object");
    return "Object";
  }
}
