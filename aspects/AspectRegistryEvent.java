package thaumcraft.api.aspects;

import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.MutableEvent;

/**
 * This event is called when Thaumcraft is ready to accept the registration of aspects associated with items or entities.
 * Add a listener to {@link #BUS} like you would for any other Forge event, for example:
 * <p><i>AspectRegistryEvent.BUS.addListener(event -&gt; event.register.registerObjectTag(...));</i>
 * <p>The <b>register</b> object contains the methods you use to register aspects.
 * <p><i>IMPORTANT: Do NOT instantiate this class or AspectEventProxy and use the methods directly - there is no guarantee that they will work like you expect.</i>
 */
public class AspectRegistryEvent extends MutableEvent {
	
	public static final EventBus<AspectRegistryEvent> BUS = EventBus.create(AspectRegistryEvent.class);
	
	/** this should always be set by TC itself - do not assign your own proxy */
	public AspectEventProxy register;

	public AspectRegistryEvent() {
		
	}
		
	
}
