package io.vertx.sqldsl.generator;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.codegen.processor.DataObjectModel;
import io.vertx.codegen.processor.Generator;
import io.vertx.codegen.processor.PropertyInfo;
import io.vertx.codegen.processor.type.AnnotationValueInfo;
import io.vertx.codegen.processor.type.ClassTypeInfo;
import io.vertx.codegen.processor.type.ParameterizedTypeInfo;
import io.vertx.codegen.processor.type.TypeInfo;
import jakarta.persistence.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.util.*;

/**
 * Generator that creates row mapper classes for data objects with relationship support. The generated mappers can handle complex object construction from SQL result
 * sets,including
 *
 * @ManyToOne and @OneToMany relationships using Jakarta persistence annotations.
 */
public class RowMapperGenerator extends Generator<DataObjectModel> {

  public RowMapperGenerator() {
    kinds = Collections.singleton("dataObject");
    name = "row_mapper_generator";
  }

  @Override
  public Collection<Class<? extends Annotation>> annotations() {
    return Collections.singletonList(DataObject.class);
  }

  @Override
  public String filename(DataObjectModel model) {
    if (model.isClass() && hasTableAnnotation(model)) {
      return model.getType().getPackageName() + "." + model.getType().getSimpleName() + "RowMapper.java";
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
    String mapperClassName = entityClassName + "RowMapper";

    writer.print("package " + packageName + ";\n");
    writer.print("\n");
    writer.print("import io.vertx.sqlclient.Row;\n");
    writer.print("import java.time.LocalDateTime;\n");
    writer.print("import java.util.List;\n");
    writer.print("import java.util.ArrayList;\n");
    writer.print("import java.util.Map;\n");
    writer.print("import java.util.HashMap;\n");
    writer.print("import java.util.function.Function;\n");
    writer.print("\n");
    writer.print("/**\n");
    writer.print(" * Row mapper for {@link " + entityClassName + "}.\n");
    writer.print(" * Handles mapping from SQL result sets including relationship resolution.\n");
    writer.print(" * NOTE: This class has been automatically generated from the {@link " + entityClassName + "} original class using Vert.x codegen.\n");
    writer.print(" */\n");
    writer.print("public class " + mapperClassName + " {\n");
    writer.print("\n");

    // Generate the main mapping method
    generateMapFromRowMethod(writer, model, entityClassName);
    writer.print("\n");

    // Generate relationship mapping methods
    generateRelationshipMappingMethods(writer, model, entityClassName);
    writer.print("\n");

    // Generate utility methods
    generateUtilityMethods(writer, model, entityClassName);

    writer.print("}\n");
    return buffer.toString();
  }

  private void generateMapFromRowMethod(PrintWriter writer, DataObjectModel model, String entityClassName) {
    writer.print("    /**\n");
    writer.print("     * Maps a single row to a " + entityClassName + " instance.\n");
    writer.print("     * @param row the SQL result row\n");
    writer.print("     * @return mapped " + entityClassName + " instance\n");
    writer.print("     */\n");
    writer.print("    public static " + entityClassName + " mapFromRow(Row row) {\n");
    writer.print("        return mapFromRow(row, \"\");\n");
    writer.print("    }\n");
    writer.print("\n");
    writer.print("    /**\n");
    writer.print("     * Maps a single row to a " + entityClassName + " instance with column prefix support.\n");
    writer.print("     * @param row the SQL result row\n");
    writer.print("     * @param prefix column prefix for joined queries\n");
    writer.print("     * @return mapped " + entityClassName + " instance\n");
    writer.print("     */\n");
    writer.print("    public static " + entityClassName + " mapFromRow(Row row, String prefix) {\n");
    writer.print("        " + entityClassName + " entity = new " + entityClassName + "();\n");
    writer.print("\n");

    // Map basic properties
    for (PropertyInfo property : model.getPropertyMap().values()) {
      if (!hasRelationshipAnnotation(property)) {
        generateBasicPropertyMapping(writer, property);
      }
    }

    writer.print("\n");
    writer.print("        return entity;\n");
    writer.print("    }\n");
  }

  private void generateBasicPropertyMapping(PrintWriter writer, PropertyInfo property) {
    String fieldName = property.getName();
    String columnName = getColumnName(property, fieldName);
    String typeName = property.getType().getName();
    String setterName = "set" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);

    writer.print("        // Map " + fieldName + " from column " + columnName + "\n");
    writer.print("        if (row.getColumnIndex(prefix + \"" + columnName + "\") != -1) {\n");

    if (isStringType(typeName)) {
      writer.print("            entity." + setterName + "(row.getString(prefix + \"" + columnName + "\"));\n");
    } else if (typeName.equals("java.lang.Long")) {
      writer.print("            entity." + setterName + "(row.getLong(prefix + \"" + columnName + "\"));\n");
    } else if (typeName.equals("java.lang.Integer")) {
      writer.print("            entity." + setterName + "(row.getInteger(prefix + \"" + columnName + "\"));\n");
    } else if (typeName.equals("java.lang.Boolean")) {
      writer.print("            entity." + setterName + "(row.getBoolean(prefix + \"" + columnName + "\"));\n");
    } else if (typeName.equals("java.lang.Double")) {
      writer.print("            entity." + setterName + "(row.getDouble(prefix + \"" + columnName + "\"));\n");
    } else if (typeName.equals("java.lang.Float")) {
      writer.print("            entity." + setterName + "(row.getFloat(prefix + \"" + columnName + "\"));\n");
    } else if (typeName.equals("java.math.BigDecimal")) {
      writer.print("            entity." + setterName + "(row.getBigDecimal(prefix + \"" + columnName + "\"));\n");
    } else if (typeName.equals("java.time.LocalDateTime")) {
      writer.print("            entity." + setterName + "(row.getLocalDateTime(prefix + \"" + columnName + "\"));\n");
    } else if (typeName.equals("java.time.LocalDate")) {
      writer.print("            entity." + setterName + "(row.getLocalDate(prefix + \"" + columnName + "\"));\n");
    } else {
      // Generic fallback with proper casting
      String simpleTypeName = getSimpleTypeName(typeName);
      writer.print("            entity." + setterName + "((" + simpleTypeName + ") row.getValue(prefix + \"" + columnName + "\"));\n");
    }

    writer.print("        }\n");
  }

  private void generateRelationshipMappingMethods(PrintWriter writer, DataObjectModel model, String entityClassName) {
    writer.print("    /**\n");
    writer.print("     * Maps rows with relationships resolved.\n");
    writer.print("     * @param rows list of SQL result rows\n");
    writer.print("     * @return list of mapped " + entityClassName + " instances with relationships\n");
    writer.print("     */\n");
    writer.print("    public static List<" + entityClassName + "> mapWithRelationships(List<Row> rows) {\n");
    writer.print("        Map<Object, " + entityClassName + "> entityMap = new HashMap<>();\n");
    writer.print("        \n");
    writer.print("        for (Row row : rows) {\n");
    writer.print("            " + entityClassName + " entity = mapFromRow(row);\n");
    writer.print("            Object primaryKey = ");

    // Find the ID field
    PropertyInfo idProperty = findIdProperty(model);
    if (idProperty != null) {
      String idGetter = "get" + Character.toUpperCase(idProperty.getName().charAt(0)) + idProperty.getName().substring(1);
      writer.print("entity." + idGetter + "();\n");
    } else {
      writer.print("entity.hashCode(); // No @Id found, using hashCode\n");
    }

    writer.print("            \n");
    writer.print("            " + entityClassName + " existingEntity = entityMap.get(primaryKey);\n");
    writer.print("            if (existingEntity == null) {\n");
    writer.print("                entityMap.put(primaryKey, entity);\n");
    writer.print("                existingEntity = entity;\n");
    writer.print("            }\n");
    writer.print("            \n");

    // Handle OneToMany relationships
    for (PropertyInfo property : model.getPropertyMap().values()) {
      AnnotationValueInfo oneToManyAnn = property.getAnnotation(OneToMany.class.getName());
      if (oneToManyAnn != null) {
        generateOneToManyMapping(writer, property, oneToManyAnn);
      }
    }

    writer.print("        }\n");
    writer.print("        \n");
    writer.print("        return new ArrayList<>(entityMap.values());\n");
    writer.print("    }\n");
  }

  private void generateOneToManyMapping(PrintWriter writer, PropertyInfo property, AnnotationValueInfo oneToManyAnn) {
    String fieldName = property.getName();
    String getterName = "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
    String setterName = "set" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);

    // Get target entity from property type or annotation
    String targetEntityType = getTargetEntityType(property, oneToManyAnn);

    writer.print("            // Handle " + fieldName + " OneToMany relationship\n");
    writer.print(
      "            " + targetEntityType + " related" + targetEntityType + " = " + targetEntityType + "RowMapper.mapFromRow(row, \"" + fieldName.toLowerCase() + "_\");\n");
    writer.print("            if (related" + targetEntityType + " != null && related" + targetEntityType + ".getId() != null) {\n");
    writer.print("                if (existingEntity." + getterName + "() == null) {\n");
    writer.print("                    existingEntity." + setterName + "(new ArrayList<>());\n");
    writer.print("                }\n");
    writer.print("                existingEntity." + getterName + "().add(related" + targetEntityType + ");\n");
    writer.print("            }\n");
  }

  private void generateUtilityMethods(PrintWriter writer, DataObjectModel model, String entityClassName) {
    writer.print("    /**\n");
    writer.print("     * Maps a list of rows to " + entityClassName + " instances.\n");
    writer.print("     * @param rows list of SQL result rows\n");
    writer.print("     * @return list of mapped " + entityClassName + " instances\n");
    writer.print("     */\n");
    writer.print("    public static List<" + entityClassName + "> mapFromRows(List<Row> rows) {\n");
    writer.print("        List<" + entityClassName + "> entities = new ArrayList<>();\n");
    writer.print("        for (Row row : rows) {\n");
    writer.print("            entities.add(mapFromRow(row));\n");
    writer.print("        }\n");
    writer.print("        return entities;\n");
    writer.print("    }\n");
    writer.print("\n");
    writer.print("    /**\n");
    writer.print("     * Creates a mapping function for use with reactive streams.\n");
    writer.print("     * @return function that maps Row to " + entityClassName + "\n");
    writer.print("     */\n");
    writer.print("    public static Function<Row, " + entityClassName + "> mapper() {\n");
    writer.print("        return " + entityClassName + "RowMapper::mapFromRow;\n");
    writer.print("    }\n");
  }

  private PropertyInfo findIdProperty(DataObjectModel model) {
    return model.getPropertyMap().values().stream()
      .filter(prop -> prop.getAnnotation(Id.class.getName()) != null)
      .findFirst()
      .orElse(null);
  }

  private boolean hasRelationshipAnnotation(PropertyInfo property) {
    return property.getAnnotation(ManyToOne.class.getName()) != null ||
      property.getAnnotation(OneToMany.class.getName()) != null;
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

  /**
   * Extracts the generic type parameter from a TypeInfo using the proper API. This handles parameterized types like List<Order>, Collection<Product>, etc.
   *
   * @param typeInfo the TypeInfo to extract generic parameter from
   * @return the simple name of the generic type parameter, or "Object" if not found
   */
  private String getGenericTypeParameter(TypeInfo typeInfo) {
    if (typeInfo.isParameterized()) {
      // Cast to ParameterizedTypeInfo to access type arguments
      if (typeInfo instanceof ParameterizedTypeInfo) {
        ParameterizedTypeInfo paramType = (ParameterizedTypeInfo) typeInfo;

        // Get the first type argument (for List<T>, get T)
        List<TypeInfo> args = paramType.getArgs();
        if (!args.isEmpty()) {
          TypeInfo firstArg = args.get(0);
          return firstArg.getSimpleName();
        }
      }
    }

    // Fallback: try to extract from the type name string representation
    String typeName = typeInfo.getName();
    if (typeName.contains("<") && typeName.contains(">")) {
      int start = typeName.indexOf('<') + 1;
      int end = typeName.lastIndexOf('>');
      String genericType = typeName.substring(start, end).trim();

      // Handle multiple parameters - take the first one
      if (genericType.contains(",")) {
        genericType = genericType.split(",")[0].trim();
      }

      // Get simple name from fully qualified name
      return getSimpleTypeName(genericType);
    }

    throw new IllegalArgumentException("Unable to extract generic type parameter from " + typeInfo);
  }

  private String getSimpleTypeName(String fullTypeName) {
    if (fullTypeName == null || fullTypeName.trim().isEmpty()) {
      return "Object";
    }

    String cleanTypeName = fullTypeName.trim();
    int lastDot = cleanTypeName.lastIndexOf('.');
    return lastDot >= 0 ? cleanTypeName.substring(lastDot + 1) : cleanTypeName;
  }

  private String getTargetEntityType(PropertyInfo property, AnnotationValueInfo oneToManyAnn) {
    // First try to get target from annotation
    Object targetEntityObj = oneToManyAnn.getMember("targetEntity");
    if (targetEntityObj instanceof ClassTypeInfo) {
      ClassTypeInfo targetEntityType = (ClassTypeInfo) targetEntityObj;
      String explicitTargetName = targetEntityType.getSimpleName();
      // Only use explicit target if it's not the default void.class
      if (!"void".equals(explicitTargetName)) {
        return explicitTargetName;
      }
    }

    // Use TypeInfo API to get generic type parameter
    return getGenericTypeParameter(property.getType());
  }
}
