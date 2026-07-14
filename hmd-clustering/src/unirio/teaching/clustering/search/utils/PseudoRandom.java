package unirio.teaching.clustering.search.utils;

import java.util.Random;

/**
 * Class representing some random facilities
 */
public class PseudoRandom
{
	private static double seed;
	
	private static double[] oldrand = new double[55];
	
	private static int jrand;
	
	private static boolean prepared = false; 

	/**
	 * Constructor
	 */
	private static void prepare()
	{
		if (!prepared)
		{
			seed = (new Random(System.nanoTime())).nextDouble();
			randomize();
			prepared = true;
		}
	}

	/**
	 * Get seed number for random and start it up 
	 */
	private static void randomize()
	{
		for (int j1 = 0; j1 <= 54; j1++)
			oldrand[j1] = 0.0;

		jrand = 0;
		warmup_random(seed);
	}

	/**
	 * Get randomize off and running 
	 */
	private static void warmup_random(double seed)
	{
		oldrand[54] = seed;
		double new_random = 0.000000001;
		double prev_random = seed;
		
		for (int j1 = 1; j1 <= 54; j1++)
		{
			int ii = (21 * j1) % 54;
			oldrand[ii] = new_random;
			new_random = prev_random - new_random;
			
			if (new_random < 0.0)
			{
				new_random += 1.0;
			}
			prev_random = oldrand[ii];
		}

		advance_random();
		advance_random();
		advance_random();
		jrand = 0;
	}

	/**
	 * Create next batch of 55 random numbers 
	 */
	private static void advance_random()
	{
		double new_random;
		
		for (int j1 = 0; j1 < 24; j1++)
		{
			new_random = oldrand[j1] - oldrand[j1 + 31];
			
			if (new_random < 0.0)
				new_random = new_random + 1.0;
			
			oldrand[j1] = new_random;
		}

		for (int j1 = 24; j1 < 55; j1++)
		{
			new_random = oldrand[j1] - oldrand[j1 - 24];

			if (new_random < 0.0)
				new_random = new_random + 1.0;
			
			oldrand[j1] = new_random;
		}
	}

	/**
	 * Fetch a single random number between 0.0 and 1.0 
	 */
	private static double randomperc()
	{
		jrand++;
	
		if (jrand >= 55)
		{
			jrand = 1;
			advance_random();
		}
		
		return ((double) oldrand[jrand]);
	}

	/**
	 * Fetch a single random integer between low and high including the bounds 
	 */
	private static int rnd(int low, int high)
	{
		int res;
	
		if (low >= high)
		{
			res = low;
		}
		else
		{
			res = low + (int) (randomperc() * (high - low + 1));
			if (res > high)
			{
				res = high;
			}
		}
		
		return res;
	}

	/**
	 * Fetch a single random real number between low and high including the bounds 
	 */
	private static double rndreal(double low, double high)
	{
		return (low + (high - low) * randomperc());
	}

	/**
	 * Returns a random double value using the PseudoRandom generator. Returns A
	 * random double value.
	 */
	public static double randDouble()
	{
		prepare();
		return rndreal(0.0, 1.0);
	}

	/**
	 * Returns a random int value between a minimum bound and maximum bound
	 * using the PseudoRandom generator.
	 */
	public static int randInt(int minBound, int maxBound)
	{
		prepare();
		return rnd(minBound, maxBound);
	}

	/**
	 * Returns a random double value between a minimum bound and a maximum bound
	 * using the PseudoRandom generator.
	 */
	public static double randDouble(double minBound, double maxBound)
	{
		prepare();
		return rndreal(minBound, maxBound);
	}
}