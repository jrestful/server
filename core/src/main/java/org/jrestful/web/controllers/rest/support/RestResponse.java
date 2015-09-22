package org.jrestful.web.controllers.rest.support;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public enum RestResponse {

  CONTINUE(HttpStatus.CONTINUE),

  SWITCHING_PROTOCOLS(HttpStatus.SWITCHING_PROTOCOLS),

  PROCESSING(HttpStatus.PROCESSING),

  CHECKPOINT(HttpStatus.CHECKPOINT),

  OK(HttpStatus.OK),

  CREATED(HttpStatus.CREATED),

  ACCEPTED(HttpStatus.ACCEPTED),

  NON_AUTHORITATIVE_INFORMATION(HttpStatus.NON_AUTHORITATIVE_INFORMATION),

  NO_CONTENT(HttpStatus.NO_CONTENT),

  RESET_CONTENT(HttpStatus.RESET_CONTENT),

  PARTIAL_CONTENT(HttpStatus.PARTIAL_CONTENT),

  MULTI_STATUS(HttpStatus.MULTI_STATUS),

  ALREADY_REPORTED(HttpStatus.ALREADY_REPORTED),

  IM_USED(HttpStatus.IM_USED),

  MULTIPLE_CHOICES(HttpStatus.MULTIPLE_CHOICES),

  MOVED_PERMANENTLY(HttpStatus.MOVED_PERMANENTLY),

  FOUND(HttpStatus.FOUND),

  SEE_OTHER(HttpStatus.SEE_OTHER),

  NOT_MODIFIED(HttpStatus.NOT_MODIFIED),

  TEMPORARY_REDIRECT(HttpStatus.TEMPORARY_REDIRECT),

  PERMANENT_REDIRECT(HttpStatus.PERMANENT_REDIRECT),

  BAD_REQUEST(HttpStatus.BAD_REQUEST),

  UNAUTHORIZED(HttpStatus.UNAUTHORIZED),

  PAYMENT_REQUIRED(HttpStatus.PAYMENT_REQUIRED),

  FORBIDDEN(HttpStatus.FORBIDDEN),

  NOT_FOUND(HttpStatus.NOT_FOUND),

  METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED),

  NOT_ACCEPTABLE(HttpStatus.NOT_ACCEPTABLE),

  PROXY_AUTHENTICATION_REQUIRED(HttpStatus.PROXY_AUTHENTICATION_REQUIRED),

  REQUEST_TIMEOUT(HttpStatus.REQUEST_TIMEOUT),

  CONFLICT(HttpStatus.CONFLICT),

  GONE(HttpStatus.GONE),

  LENGTH_REQUIRED(HttpStatus.LENGTH_REQUIRED),

  PRECONDITION_FAILED(HttpStatus.PRECONDITION_FAILED),

  PAYLOAD_TOO_LARGE(HttpStatus.PAYLOAD_TOO_LARGE),

  URI_TOO_LONG(HttpStatus.URI_TOO_LONG),

  UNSUPPORTED_MEDIA_TYPE(HttpStatus.UNSUPPORTED_MEDIA_TYPE),

  REQUESTED_RANGE_NOT_SATISFIABLE(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE),

  EXPECTATION_FAILED(HttpStatus.EXPECTATION_FAILED),

  I_AM_A_TEAPOT(HttpStatus.I_AM_A_TEAPOT),

  UNPROCESSABLE_ENTITY(HttpStatus.UNPROCESSABLE_ENTITY),

  LOCKED(HttpStatus.LOCKED),

  FAILED_DEPENDENCY(HttpStatus.FAILED_DEPENDENCY),

  UPGRADE_REQUIRED(HttpStatus.UPGRADE_REQUIRED),

  PRECONDITION_REQUIRED(HttpStatus.PRECONDITION_REQUIRED),

  TOO_MANY_REQUESTS(HttpStatus.TOO_MANY_REQUESTS),

  REQUEST_HEADER_FIELDS_TOO_LARGE(HttpStatus.REQUEST_HEADER_FIELDS_TOO_LARGE),

  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR),

  NOT_IMPLEMENTED(HttpStatus.NOT_IMPLEMENTED),

  BAD_GATEWAY(HttpStatus.BAD_GATEWAY),

  SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE),

  GATEWAY_TIMEOUT(HttpStatus.GATEWAY_TIMEOUT),

  HTTP_VERSION_NOT_SUPPORTED(HttpStatus.HTTP_VERSION_NOT_SUPPORTED),

  VARIANT_ALSO_NEGOTIATES(HttpStatus.VARIANT_ALSO_NEGOTIATES),

  INSUFFICIENT_STORAGE(HttpStatus.INSUFFICIENT_STORAGE),

  LOOP_DETECTED(HttpStatus.LOOP_DETECTED),

  BANDWIDTH_LIMIT_EXCEEDED(HttpStatus.BANDWIDTH_LIMIT_EXCEEDED),

  NOT_EXTENDED(HttpStatus.NOT_EXTENDED),

  NETWORK_AUTHENTICATION_REQUIRED(HttpStatus.NETWORK_AUTHENTICATION_REQUIRED);

  private final HttpStatus status;

  private RestResponse(HttpStatus status) {
    this.status = status;
  }

  public <T> ResponseEntity<T> build(T body) {
    return new ResponseEntity<>(body, status);
  }

  public <T> ResponseEntity<T> build() {
    return new ResponseEntity<>(status);
  }

  public static <T> ResponseEntity<T> ok(T body) {
    return OK.build(body);
  }

  public static <T> ResponseEntity<T> created(T body) {
    return CREATED.build(body);
  }

  public static <T> ResponseEntity<T> accepted(T body) {
    return ACCEPTED.build(body);
  }

  public static <T> ResponseEntity<T> noContent(T body) {
    return NO_CONTENT.build(body);
  }

  public static <T> ResponseEntity<T> resetContent(T body) {
    return RESET_CONTENT.build(body);
  }

  public static <T> ResponseEntity<T> notFound(T body) {
    return NOT_FOUND.build(body);
  }

  public static <T> ResponseEntity<T> conflict(T body) {
    return CONFLICT.build(body);
  }

  public static <T> ResponseEntity<T> gone(T body) {
    return GONE.build(body);
  }

  public static <T> ResponseEntity<T> ok() {
    return OK.build();
  }

  public static <T> ResponseEntity<T> created() {
    return CREATED.build();
  }

  public static <T> ResponseEntity<T> accepted() {
    return ACCEPTED.build();
  }

  public static <T> ResponseEntity<T> noContent() {
    return NO_CONTENT.build();
  }

  public static <T> ResponseEntity<T> resetContent() {
    return RESET_CONTENT.build();
  }

  public static <T> ResponseEntity<T> notFound() {
    return NOT_FOUND.build();
  }

  public static <T> ResponseEntity<T> conflict() {
    return CONFLICT.build();
  }

  public static <T> ResponseEntity<T> gone() {
    return GONE.build();
  }

}
