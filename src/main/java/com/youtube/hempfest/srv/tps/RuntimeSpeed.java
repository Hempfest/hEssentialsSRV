package com.youtube.hempfest.srv.tps;

import org.bukkit.Bukkit;

public class RuntimeSpeed
		implements Runnable
{
	public static int TICK_COUNT = 0;
	public static long[] TICKS = new long['»'];
	public static long LAST_TICK = 0L;

	public static double getTPS()
	{
		return getTPS(100);
	}

	public static double getTPS(int ticks)
	{
		try
		{
			if (TICK_COUNT < ticks) {
				return 20.0D;
			}
			int target = (TICK_COUNT - 1 - ticks) % TICKS.length;
			long elapsed = System.currentTimeMillis() - TICKS[target];

			return ticks / (elapsed / 1000.0D);
		}
		catch (Exception e)
		{
			if ((e instanceof ArrayIndexOutOfBoundsException)) {
				return 20.0D;
			}
			Bukkit.getLogger().severe("[AntiLag] An error occured whilst retrieving the TPS");
		}
		return 20.0D;
	}

	public static long getElapsed(int tickID)
	{

		long time = TICKS[(tickID % TICKS.length)];
		return System.currentTimeMillis() - time;
	}

	public void run()
	{
		TICKS[(TICK_COUNT % TICKS.length)] = System.currentTimeMillis();

		TICK_COUNT += 1;
	}
}