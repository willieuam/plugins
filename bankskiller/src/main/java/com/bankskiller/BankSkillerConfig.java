package com.bankskiller;

import net.runelite.client.config.*;

@ConfigGroup("bankskiller")
public interface BankSkillerConfig extends Config
{
	@ConfigItem(keyName = "startButton",
			name = "Start",
			description = "",
			position = 1
	)
	default Button startButton() { return new Button(); }

	@ConfigItem(
			keyName = "stopButton",
			name = "Stop",
			description = "",
			position = 2
	)
	default Button stopButton() { return new Button(); }

	@ConfigItem(
			keyName = "resetButton",
			name = "Reset",
			description = "",
			position = 3
	)
	default Button resetButton() { return new Button(); }

	@ConfigItem(
			keyName = "actionsScript",
			name = "Actions",
			description = "The script of actions to loop, one per line.",
			position = 4
	)
	default String actionsScript() { return ""; }

	@ConfigItem(
			keyName = "minDelay",
			name = "Min Tick Delay",
			description = "Min amount of ticks for random delays in actions.",
			position = 5
	)
	default int minDelay() { return 0; }

	@ConfigItem(
			keyName = "maxDelay",
			name = "Max Tick Delay",
			description = "Max amount of ticks for random delays in actions.",
			position = 6
	)
	default int maxDelay() { return 3; }

	@ConfigItem(
			keyName = "minBreak",
			name = "Min Tick Break",
			description = "Min amount of ticks for random breaks.",
			position = 7
	)
	default int minBreak() { return 200; }

	@ConfigItem(
			keyName = "maxBreak",
			name = "Max Tick Break",
			description = "Max amount of ticks for random breaks.",
			position = 8
	)
	default int maxBreak() { return 500; }

	@ConfigItem(
			keyName = "breakChance",
			name = "Break Chance",
			description = "Chance for a break to occur. Floating point number between 0 and 1.",
			position = 9
	)
	default double breakChance() { return 0.01; }
}
