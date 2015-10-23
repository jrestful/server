package org.jrestful.web.beans;

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

  public static <T> ResponseEntity<T> nonAuthoritativeInformation(T body) {
    return NON_AUTHORITATIVE_INFORMATION.build(body);
  }

  public static <T> ResponseEntity<T> noContent(T body) {
    return NO_CONTENT.build(body);
  }

  public static <T> ResponseEntity<T> resetContent(T body) {
    return RESET_CONTENT.build(body);
  }

  public static <T> ResponseEntity<T> partialContent(T body) {
    return PARTIAL_CONTENT.build(body);
  }

  public static <T> ResponseEntity<T> multiStatus(T body) {
    return MULTI_STATUS.build(body);
  }

  public static <T> ResponseEntity<T> alreadyReported(T body) {
    return ALREADY_REPORTED.build(body);
  }

  public static <T> ResponseEntity<T> imUsed(T body) {
    return IM_USED.build(body);
  }

  public static <T> ResponseEntity<T> badRequest(T body) {
    return BAD_REQUEST.build(body);
  }

  public static <T> ResponseEntity<T> unauthorized(T body) {
    return UNAUTHORIZED.build(body);
  }

  public static <T> ResponseEntity<T> paymentRequired(T body) {
    return PAYMENT_REQUIRED.build(body);
  }

  public static <T> ResponseEntity<T> forbidden(T body) {
    return FORBIDDEN.build(body);
  }

  public static <T> ResponseEntity<T> notFound(T body) {
    return NOT_FOUND.build(body);
  }

  public static <T> ResponseEntity<T> methodNotAllowed(T body) {
    return METHOD_NOT_ALLOWED.build(body);
  }

  public static <T> ResponseEntity<T> notAcceptable(T body) {
    return NOT_ACCEPTABLE.build(body);
  }

  public static <T> ResponseEntity<T> proxyAuthenticationRequired(T body) {
    return PROXY_AUTHENTICATION_REQUIRED.build(body);
  }

  public static <T> ResponseEntity<T> requestTimeout(T body) {
    return REQUEST_TIMEOUT.build(body);
  }

  public static <T> ResponseEntity<T> conflict(T body) {
    return CONFLICT.build(body);
  }

  public static <T> ResponseEntity<T> gone(T body) {
    return GONE.build(body);
  }

  public static <T> ResponseEntity<T> lengthRequired(T body) {
    return LENGTH_REQUIRED.build(body);
  }

  public static <T> ResponseEntity<T> preconditionFailed(T body) {
    return PRECONDITION_FAILED.build(body);
  }

  public static <T> ResponseEntity<T> payloadTooLarge(T body) {
    return PAYLOAD_TOO_LARGE.build(body);
  }

  public static <T> ResponseEntity<T> uriTooLong(T body) {
    return URI_TOO_LONG.build(body);
  }

  public static <T> ResponseEntity<T> unsupportedMediaType(T body) {
    return UNSUPPORTED_MEDIA_TYPE.build(body);
  }

  public static <T> ResponseEntity<T> requestedRangeNotSatisfiable(T body) {
    return REQUESTED_RANGE_NOT_SATISFIABLE.build(body);
  }

  public static <T> ResponseEntity<T> expectationFailed(T body) {
    return EXPECTATION_FAILED.build(body);
  }

  public static <T> ResponseEntity<T> iAmATeapot(T body) {
    return I_AM_A_TEAPOT.build(body);
  }

  public static <T> ResponseEntity<T> unprocessableEntity(T body) {
    return UNPROCESSABLE_ENTITY.build(body);
  }

  public static <T> ResponseEntity<T> locked(T body) {
    return LOCKED.build(body);
  }

  public static <T> ResponseEntity<T> failedDependency(T body) {
    return FAILED_DEPENDENCY.build(body);
  }

  public static <T> ResponseEntity<T> upgradeRequired(T body) {
    return UPGRADE_REQUIRED.build(body);
  }

  public static <T> ResponseEntity<T> preconditionRequired(T body) {
    return PRECONDITION_REQUIRED.build(body);
  }

  public static <T> ResponseEntity<T> tooManyRequests(T body) {
    return TOO_MANY_REQUESTS.build(body);
  }

  public static <T> ResponseEntity<T> requestHeaderFieldsTooLarge(T body) {
    return REQUEST_HEADER_FIELDS_TOO_LARGE.build(body);
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

  public static <T> ResponseEntity<T> nonAuthoritativeInformation() {
    return NON_AUTHORITATIVE_INFORMATION.build();
  }

  public static <T> ResponseEntity<T> noContent() {
    return NO_CONTENT.build();
  }

  public static <T> ResponseEntity<T> resetContent() {
    return RESET_CONTENT.build();
  }

  public static <T> ResponseEntity<T> partialContent() {
    return PARTIAL_CONTENT.build();
  }

  public static <T> ResponseEntity<T> multiStatus() {
    return MULTI_STATUS.build();
  }

  public static <T> ResponseEntity<T> alreadyReported() {
    return ALREADY_REPORTED.build();
  }

  public static <T> ResponseEntity<T> imUsed() {
    return IM_USED.build();
  }

  public static <T> ResponseEntity<T> badRequest() {
    return BAD_REQUEST.build();
  }

  public static <T> ResponseEntity<T> unauthorized() {
    return UNAUTHORIZED.build();
  }

  public static <T> ResponseEntity<T> paymentRequired() {
    return PAYMENT_REQUIRED.build();
  }

  public static <T> ResponseEntity<T> forbidden() {
    return FORBIDDEN.build();
  }

  public static <T> ResponseEntity<T> notFound() {
    return NOT_FOUND.build();
  }

  public static <T> ResponseEntity<T> methodNotAllowed() {
    return METHOD_NOT_ALLOWED.build();
  }

  public static <T> ResponseEntity<T> notAcceptable() {
    return NOT_ACCEPTABLE.build();
  }

  public static <T> ResponseEntity<T> proxyAuthenticationRequired() {
    return PROXY_AUTHENTICATION_REQUIRED.build();
  }

  public static <T> ResponseEntity<T> requestTimeout() {
    return REQUEST_TIMEOUT.build();
  }

  public static <T> ResponseEntity<T> conflict() {
    return CONFLICT.build();
  }

  public static <T> ResponseEntity<T> gone() {
    return GONE.build();
  }

  public static <T> ResponseEntity<T> lengthRequired() {
    return LENGTH_REQUIRED.build();
  }

  public static <T> ResponseEntity<T> preconditionFailed() {
    return PRECONDITION_FAILED.build();
  }

  public static <T> ResponseEntity<T> payloadTooLarge() {
    return PAYLOAD_TOO_LARGE.build();
  }

  public static <T> ResponseEntity<T> uriTooLong() {
    return URI_TOO_LONG.build();
  }

  public static <T> ResponseEntity<T> unsupportedMediaType() {
    return UNSUPPORTED_MEDIA_TYPE.build();
  }

  public static <T> ResponseEntity<T> requestedRangeNotSatisfiable() {
    return REQUESTED_RANGE_NOT_SATISFIABLE.build();
  }

  public static <T> ResponseEntity<T> expectationFailed() {
    return EXPECTATION_FAILED.build();
  }

  public static <T> ResponseEntity<T> iAmATeapot() {
    return I_AM_A_TEAPOT.build();
  }

  public static <T> ResponseEntity<T> unprocessableEntity() {
    return UNPROCESSABLE_ENTITY.build();
  }

  public static <T> ResponseEntity<T> locked() {
    return LOCKED.build();
  }

  public static <T> ResponseEntity<T> failedDependency() {
    return FAILED_DEPENDENCY.build();
  }

  public static <T> ResponseEntity<T> upgradeRequired() {
    return UPGRADE_REQUIRED.build();
  }

  public static <T> ResponseEntity<T> preconditionRequired() {
    return PRECONDITION_REQUIRED.build();
  }

  public static <T> ResponseEntity<T> tooManyRequests() {
    return TOO_MANY_REQUESTS.build();
  }

  public static <T> ResponseEntity<T> requestHeaderFieldsTooLarge() {
    return REQUEST_HEADER_FIELDS_TOO_LARGE.build();
  }

}
