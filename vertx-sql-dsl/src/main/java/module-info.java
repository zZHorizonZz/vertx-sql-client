module io.vertx.sql.dsl {

  requires static io.vertx.codegen.processor;
  requires static io.vertx.codegen.json;
  requires static io.vertx.codegen.api;
  requires static java.compiler;
  requires static io.vertx.docgen;

  requires io.vertx.sql.client;
  requires io.vertx.sql.client.templates;
  requires io.vertx.core;
  requires jakarta.persistence;

  exports io.vertx.sqldsl.repository;
  exports io.vertx.sqldsl;

}
