package com.google.gwt.query.client.plugins;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.animation.client.AnimationScheduler;
import com.google.gwt.animation.client.AnimationScheduler.AnimationCallback;
import com.google.gwt.animation.client.AnimationScheduler.AnimationHandle;
import com.google.gwt.dom.client.Element;
import com.google.gwt.query.client.Function;
import com.google.gwt.query.client.GQ;
import com.google.gwt.query.client.GQuery;
import com.google.gwt.query.client.builders.JsonBuilder;
import com.google.gwt.query.client.plugins.events.EventsListener;
import com.google.gwt.query.client.plugins.events.SpecialEvent;
import com.google.gwt.user.client.Event;

/**
 * Add resize event support to any DOM element.
 * Based on:
 * http://www.backalleycoder.com/2013/03/18/cross-browser-event-based-element-resize-detection/
 * http://benalman.com/projects/jquery-resize-plugin/
 * 
 * It uses modern animationFrame to check when any observed element changes its size.
 *
 * @author Manolo Carrasco
 */
public class Resize extends GQuery {
  private static final String DATA = Resize.class.getName();
  private static final String TYPE = "resize";

  public interface ResizeData extends JsonBuilder {
    ResizeData width(int i);
    ResizeData height(int i);
    int width();
    int height();
  }
  
  public static final Class<Resize> Resize = registerPlugin(Resize.class, new Plugin<Resize>() {
    public Resize init(GQuery gq) {
      return new Resize(gq);
    }
  });
  
  static {
    EventsListener.special.put(TYPE, new ResizeSpecialEvent());
  }

  public Resize(GQuery gq) {
    super(gq);
  }
  
  public static class ResizeSpecialEvent implements SpecialEvent {
    private List<Element> elements = new ArrayList<Element>();
    
    private AnimationHandle timer;

    private void resize() {
      for (Element e : elements) {
        GQuery g = $(e);
        ResizeData d = g.data(DATA);
        if (g.width() != d.width() || g.height() != d.height()) {
          d.width(g.width()).height(g.height());
          g.trigger(TYPE);
        }
      }
    }
    
    private void loop() {
      timer = AnimationScheduler.get().requestAnimationFrame(new AnimationCallback() {
        public void execute(double timestamp) {
          resize();
          loop();
        }
      });
    }
    

    @Override
    public void add(Element e, String eventType, String nameSpace, Object data, Function f) {
    }

    @Override
    public boolean hasHandlers(Element e) {
      return EventsListener.getInstance(e).hasHandlers(Event.getTypeInt(TYPE), TYPE, null);
    }

    @Override
    public void remove(Element e, String eventType, String nameSpace, Function f) {
    }

    @Override
    public boolean setup(Element e) {
      if (e != window && !elements.contains(e)) {
        elements.add(e);
        GQuery g = $(e);
        ResizeData d = GQ.create(ResizeData.class).width(g.width()).height(g.height());
        g.data(DATA, d);
        if (timer == null) {
          loop();
        }
      }
      return false;
    }

    @Override
    public boolean tearDown(Element e) {
      $(e).removeData(DATA);
      elements.remove(e);
      if (elements.size() == 0 && timer != null) {
        timer.cancel();
        timer = null;
      }
      return false;
    }
  }

  public GQuery resize(Function f) {
    return on(TYPE, f);
  }
}
