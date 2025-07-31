package io.vertx.sqldsl.generator;

import io.vertx.codegen.processor.Generator;
import io.vertx.codegen.processor.GeneratorLoader;

import javax.annotation.processing.ProcessingEnvironment;
import java.util.stream.Stream;

/**
 * Loader for the row mapper generator.
 */
public class RowMapperGenLoader implements GeneratorLoader {

  @Override
  public Stream<Generator<?>> loadGenerators(ProcessingEnvironment processingEnv) {
    return Stream.of(new RowMapperGenerator());
  }
}