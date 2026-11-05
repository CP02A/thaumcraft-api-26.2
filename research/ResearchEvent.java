package thaumcraft.api.research;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.bus.CancellableEventBus;
import net.minecraftforge.eventbus.api.event.MutableEvent;
import net.minecraftforge.eventbus.api.event.characteristic.Cancellable;
import thaumcraft.api.capabilities.IPlayerKnowledge.EnumKnowledgeType;

/**
 * These events trigger whenever a players gains knowledge or progresses research.
 * They can be cancelled to prevent the research gain.
 * <p>Each event type is posted to its own bus, for example:
 * <p><i>ResearchEvent.Research.BUS.addListener(event -&gt; ...);</i><br>
 * <i>ResearchEvent.Knowledge.BUS.addListener(event -&gt; ...);</i>
 * <p>To cancel, register a <code>Predicate</code> listener that returns true, see {@link CancellableEventBus#addListener(java.util.function.Predicate)}.
 */
public class ResearchEvent extends MutableEvent implements Cancellable {

	private final Player player;

	public ResearchEvent(Player player)
    {
        this.player = player;
    }

    public Player getPlayer()
    {
        return player;
    }


	public static class Knowledge extends ResearchEvent
    {
		public static final CancellableEventBus<Knowledge> BUS = CancellableEventBus.create(Knowledge.class);

		private final EnumKnowledgeType type;
		private final ResearchCategory category;
		private final int amount;

		public Knowledge(Player player, EnumKnowledgeType type, ResearchCategory category, int amount)
        {
            super(player);
            this.type = type;
            this.category = category;
            this.amount = amount;
        }

		public EnumKnowledgeType getType() {
			return type;
		}

		public ResearchCategory getCategory() {
			return category;
		}

		public int getAmount() {
			return amount;
		}

    }

	public static class Research extends ResearchEvent
    {
		public static final CancellableEventBus<Research> BUS = CancellableEventBus.create(Research.class);

		private final String researchKey;

		public Research(Player player, String researchKey)
        {
            super(player);
            this.researchKey = researchKey;
        }

		public String getResearchKey() {
			return researchKey;
		}
    }

}
