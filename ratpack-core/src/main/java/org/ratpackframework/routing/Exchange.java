package org.ratpackframework.routing;

import org.ratpackframework.context.Context;
import org.ratpackframework.http.Request;
import org.ratpackframework.http.Response;
import org.ratpackframework.session.Session;

import java.util.Map;

public interface Exchange {

  Request getRequest();

  Response getResponse();

  Context getContext();

  void next();

  void next(Handler... handlers);

  void next(Iterable<Handler> handlers);

  void nextWithContext(Object context, Handler... handlers);

  void nextWithContext(Object context, Iterable<Handler> handlers);

  Map<String, String> getPathTokens();

  Map<String, String> getAllPathTokens();

  // TODO - not sure if this is important enough to be on this interface
  Session getSession();

  void error(Exception exception);

}
