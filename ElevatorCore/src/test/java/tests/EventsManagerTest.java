package tests;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import engine.ButtonEvent.ButtonEventObserver;
import engine.ElevatorEvent;
import engine.ElevatorEvent.ElevatorEventObserver;
import engine.ButtonEvent;
import engine.EventsManager;
import engine.FloorSensorEvent;
import engine.FloorSensorEvent.FloorSensorEventObserver;

import org.junit.Assert;

/**
 * Tests {@link EventsManager} and some events.
 * 
 * @author SSE
 */
public class EventsManagerTest {
    
    /**
     * Tests {@link ButtonEvent}.
     */
    @Test
    public void testButtonEvent() {
        AtomicInteger count = new AtomicInteger();
        ButtonEventObserver obs = new ButtonEventObserver() {
            
            @Override
            public void notifyReceived(ButtonEvent event) {
                count.incrementAndGet();
            }
        };
        EventsManager.BUTTONS.addObserver(null); // ignore
        EventsManager.BUTTONS.removeAllObservers();
        EventsManager.BUTTONS.addObserverIfUnknown(obs);
        EventsManager.BUTTONS.addObserverIfUnknown(obs); // shall not happen twice
        EventsManager.BUTTONS.setDebug(true);
        
        ButtonEvent evt = new ButtonEvent(0, 1, ButtonEvent.Kind.CANCEL, 2, true, false);
        Assert.assertEquals(0, evt.getElevator());
        Assert.assertEquals(1, evt.getFloor());
        Assert.assertEquals(ButtonEvent.Kind.CANCEL, evt.getKind());
        Assert.assertEquals(2, evt.getValue());
        Assert.assertEquals(true, evt.isHightlighed());
        Assert.assertEquals(false, evt.isInside());
        
        EventsManager.BUTTONS.sendEvent(evt);
        Assert.assertEquals(1, count.get());
        EventsManager.BUTTONS.setDebug(false);
        EventsManager.BUTTONS.sendEvent(evt);
        Assert.assertEquals(2, count.get());
        EventsManager.BUTTONS.removeObserverIf(o -> o.getHandlingInfo() == null); // default null
        EventsManager.BUTTONS.removeObserver(obs);
    }

    /**
     * Tests {@link ElevatorEvent}.
     */
    @Test
    public void testElevatorEvent() {
        AtomicInteger count = new AtomicInteger();
        ElevatorEventObserver obs = new ElevatorEventObserver() {
            
            @Override
            public void notifyReceived(ElevatorEvent event) {
                count.incrementAndGet();
            }

            @Override
            public Object getHandlingInfo() {
                return "MyEvent";
            }            
            
        };
        EventsManager.ELEVATORS.addObserver(null); // ignore
        EventsManager.ELEVATORS.removeAllObservers();
        EventsManager.ELEVATORS.addObserverIfUnknown(obs);
        EventsManager.ELEVATORS.addObserverIfUnknown(obs); // shall not happen twice
        EventsManager.ELEVATORS.setDebug(true);
        
        ElevatorEvent evt = new ElevatorEvent(1, 2, ElevatorEvent.Kind.DOORS_CLOSED);
        Assert.assertEquals(1, evt.getElevator());
        Assert.assertEquals(2, evt.getFloor());
        Assert.assertEquals(ElevatorEvent.Kind.DOORS_CLOSED, evt.getKind());
        
        EventsManager.ELEVATORS.sendEvent(evt);
        Assert.assertEquals(1, count.get());
        EventsManager.ELEVATORS.setDebug(false);
        EventsManager.ELEVATORS.sendEvent(evt);
        Assert.assertEquals(2, count.get());
        EventsManager.ELEVATORS.removeObserverIf(o -> o.getHandlingInfo() != null); // shall hold
        EventsManager.ELEVATORS.removeObserver(obs); // no effect
    }

    /**
     * Tests {@link FloorSensorEvent}.
     */
    @Test
    public void testFloorSensorEvent() {
        AtomicInteger count = new AtomicInteger();
        FloorSensorEventObserver obs = new FloorSensorEventObserver() {
            
            @Override
            public void notifyReceived(FloorSensorEvent event) {
                count.incrementAndGet();
            }
            
        };
        EventsManager.FLOOR_SENSORS.addObserver(null); // ignore
        EventsManager.FLOOR_SENSORS.removeAllObservers();
        EventsManager.FLOOR_SENSORS.addObserverIfUnknown(obs);
        EventsManager.FLOOR_SENSORS.addObserverIfUnknown(obs); // shall not happen twice
        EventsManager.FLOOR_SENSORS.setDebug(true);
        
        FloorSensorEvent evt = new FloorSensorEvent(2, 3, FloorSensorEvent.Position.TOP);
        Assert.assertEquals(2, evt.getElevator());
        Assert.assertEquals(3, evt.getFloor());
        Assert.assertEquals(FloorSensorEvent.Position.TOP, evt.getPosition());
        
        EventsManager.FLOOR_SENSORS.sendEvent(evt);
        Assert.assertEquals(1, count.get());
        EventsManager.FLOOR_SENSORS.setDebug(false);
        EventsManager.FLOOR_SENSORS.sendEvent(evt);
        Assert.assertEquals(2, count.get());
        EventsManager.FLOOR_SENSORS.removeObserverIf(o -> o.declaringClass() != EventsManagerTest.class); // shall hold
        EventsManager.FLOOR_SENSORS.removeObserver(obs); // no effect
    }
    
}
