package com.google.gwt.query.client.plugins;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.query.client.Function;
import com.google.gwt.query.client.GQuery;
import com.google.gwt.query.client.Properties;
import com.google.gwt.query.client.js.JsUtils;
import com.google.gwt.query.client.plugins.events.EventsListener;
import com.google.gwt.query.client.plugins.events.SpecialEvent;
import com.google.gwt.user.client.Event;

/**
 * Add resize event support to any DOM element.
 * Based on:
 * http://www.backalleycoder.com/2013/03/18/cross-browser-event-based-element-resize-detection/
 * http://benalman.com/projects/jquery-resize-plugin/
 *
 * It uses a children object element whose content window fills all the
 * element area, and it is used to listen to resize events.
 * Hence, it's mandatory that the observed element layout allows the contained
 * element grow when it does. No timers are needed, so there are no performance
 * penalties.
 *
 * @author Manolo Carrasco
 */
public class Resize extends GQuery {
  private static final String RESIZE = "resize";
  private static final Properties containerCss = $$("position: relative");
  private static final String objectHtml = "<object type='text/html' style='display:block;position:absolute;top:0;left:0;height:100%;width:100%;overflow:hidden;pointer-events:none;z-index:-1;'>";

  public static final Class<Resize> Resize = registerPlugin(Resize.class, new Plugin<Resize>() {
    public Resize init(GQuery gq) {
      return new Resize(gq);
    }
  });

  static {
    EventsListener.special.put(RESIZE, new ResizeSpecialEvent());
  }

  public Resize(GQuery gq) {
    super(gq);
  }

  public static class ResizeSpecialEvent implements SpecialEvent {
    private List<Element> elements = new ArrayList<Element>();

    private void appendObserverObject(final Element e) {
      GQuery $e = $(e);
      if ($e.find(".resize-object").isEmpty()) {
        final GQuery $o = $(objectHtml);
        $o.on("load", new Function(){
          Function f = new Function() {
            public void f() {
              $(e).trigger(RESIZE);
            }
          };
          public void f() {
              Element win = $(getStyleImpl().getContentDocument($o.get(0))).prop("defaultView");
              $(win).off(RESIZE, f).on(RESIZE, f);
              f.f();
          }
        });
        if (browser.msie || browser.mozilla) {
          $o.appendTo(e);
          $o.prop("data", "about:blank");
        } else {
          $o.prop("data", "about:blank");
          $o.appendTo(e);
        }

        // Try to fix container position in case user didn't configure correctly
        if ($(e).css("position", true).toLowerCase().matches("initial|static")) {
          $e.css(containerCss);
        }
      }
    }

    private void removeObserverObject(Element e) {
      if (isResizableElement(e)) $(e).find(".resize-object").off(RESIZE).remove();
    }

    @Override
    public void add(Element e, String eventType, String nameSpace, Object data, Function f) {
    }

    @Override
    public boolean hasHandlers(Element e) {
      return EventsListener.getInstance(e).hasHandlers(Event.getTypeInt(RESIZE), RESIZE, null);
    }

    @Override
    public void remove(Element e, String eventType, String nameSpace, Function f) {
    }

    @Override
    public boolean setup(final Element e) {
      if (isResizableElement(e)) appendObserverObject(e);
      return false;
    }

    @Override
    public boolean tearDown(Element e) {
      removeObserverObject(e);
      return false;
    }

    private boolean isResizableElement(Element e) {
      return !JsUtils.isWindow(e) && e.getNodeType() == Node.ELEMENT_NODE && !elements.contains(e);
    }
  }
}
