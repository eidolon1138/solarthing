package me.retrodaredevil.solarthing.packets;

/**
 * Represents a mode where only one of its kind can be active
 */
public interface CodeMode extends Mode {
	/**
	 * NOTE: -1 <em>usually</em> represents an unknown mode
	 * @return The code representing the mode
	 */
	int getValueCode();

	@Override
	default boolean isActive(int valueCode){
		return getValueCode() == valueCode;
	}

}
