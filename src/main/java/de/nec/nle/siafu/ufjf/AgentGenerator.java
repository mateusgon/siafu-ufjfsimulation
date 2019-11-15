/*
 * Copyright NEC Europe Ltd. 2006-2007
 * 
 * This file is part of the context simulator called Siafu.
 * 
 * Siafu is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * Siafu is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.nec.nle.siafu.ufjf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import de.nec.nle.siafu.model.Agent;
import de.nec.nle.siafu.model.World;
import de.nec.nle.siafu.types.EasyTime;
import de.nec.nle.siafu.types.Publishable;
import de.nec.nle.siafu.types.Text;

import de.nec.nle.siafu.ufjf.Constants;

/**
 * Utility class to generate an agent with randomized parameters.
 * 
 * @author Miquel Martin
 * 
 */
final class AgentGenerator {

	/**
	 * A random number generator.
	 */
	private static Random rand = new Random();

	/** Prevent the class from being instantiated. */
	private AgentGenerator() {
	}

	/**
	 * Create a random agent.
	 * 
	 * @param world the world to create it in
	 * @return the new agent
	 */
	private static Agent createAgent(final World world, Integer position, Boolean Wander) {
		try {
			Agent a = null;
			if (!Wander)
			{
				a = setPlaceAndInfoToStart(a, position, world);
			}
			else
			{
				a = setPlaceAndInfoToStartWander(a, position, world);
				setWanderTime(a);
			}
			a.setVisible(false);
			
			String ocupacao = a.get("Occupation").toString();
			if(ocupacao.equals("Student"))
			{
				setHours(a);
			}
			else if (ocupacao.equals("Worker"))
			{
				setHoursOfWork(a);
			}
			a.set("TemporaryDestination", new Text("None"));
			a.set(Constants.Fields.ACTIVITY, Constants.Activity.WAITING);
			a.setSpeed(7 + rand.nextInt(Constants.DEFAULT_SPEED));
			return a;
		} catch (Exception e) {
			throw new RuntimeException(
					"You didn't define the places", e);
		}
	}
		
	private static Agent setPlaceAndInfoToStart(Agent a, Integer position, final World world) throws Exception
	{
		try {
			if (position % 2 == 0)
			{
				a = new Agent(world.getRandomPlaceOfType("PortaoSul").getPos(), "HumanYellow", world);
				a.set("Occupation", new Text(rand.nextInt(2) == 0 ? "Student" : "Worker"));
				a.set("InitialPlace", new Text("PortaoSul"));
			}
			else
			{
				a = new Agent(world.getRandomPlaceOfType("PortaoNorte").getPos(), "HumanBlue", world);
				a.set("Occupation", new Text(rand.nextInt(2) == 0 ? "Student" : "Worker"));
				a.set("InitialPlace", new Text("PortaoNorte"));
			}
			a.set("HourWander", new Text("None"));
		}
		catch (Exception ex)
		{
			throw ex;
		}
		return a;
	}
	
	private static Agent setPlaceAndInfoToStartWander(Agent a, Integer position, final World world) throws Exception
	{
		try {
			if (position % 2 == 0)
			{
				a = new Agent(world.getRandomPlaceOfType("PortaoSul").getPos(), "HumanBrown", world);
				a.set("InitialPlace", new Text("PortaoSul"));
			}
			else
			{
				a = new Agent(world.getRandomPlaceOfType("PortaoNorte").getPos(), "HumanBrown", world);
				a.set("InitialPlace", new Text("PortaoNorte"));
			}
			a.set("Occupation", new Text("Wander"));
			int hourWander = rand.nextInt(24);
			while(hourWander < 7 || hourWander > 22)
			{
				hourWander = rand.nextInt(24);
			}
			a.set("HourWander", new EasyTime(hourWander, 0));

		}
		catch (Exception ex)
		{
			throw ex;
		}
		return a;
	}
	
	private static Agent setHours(Agent a)
	{
		a.set(Constants.Fields.WORK_HOUR, Constants.DEFAULT_HOUR);			
		Integer classTypes = rand.nextInt(3);
		if (classTypes == 0)
		{
			a.set(Constants.Fields.CLASS_HOUR, Constants.FIRST_CLASS_START);
			a.set(Constants.Fields.LUNCH_HOUR, Constants.LUNCH);
			a.set(Constants.Fields.DINNER_HOUR, Constants.DEFAULT_HOUR);
		}
		else if (classTypes == 1)
		{
			a.set(Constants.Fields.CLASS_HOUR, Constants.SECOND_CLASS_START);
			a.set(Constants.Fields.LUNCH_HOUR, Constants.LUNCH);
			a.set(Constants.Fields.DINNER_HOUR, Constants.DINNER);
		}
		else
		{
			a.set(Constants.Fields.CLASS_HOUR, Constants.THIRD_CLASS_START);			
			a.set(Constants.Fields.LUNCH_HOUR, Constants.DEFAULT_HOUR);
			a.set(Constants.Fields.DINNER_HOUR, Constants.DINNER);
		}				
		a.set("Wander", new Text("No"));
		return a;
	}
	
	private static Agent setHoursOfWork(Agent a)
	{
		a.set(Constants.Fields.CLASS_HOUR, Constants.DEFAULT_HOUR);			
		a.set(Constants.Fields.LUNCH_HOUR, Constants.DEFAULT_HOUR);
		a.set(Constants.Fields.DINNER_HOUR, Constants.DEFAULT_HOUR);
		Integer classTypes = rand.nextInt(2);
		if (classTypes == 0)
		{
			a.set(Constants.Fields.WORK_HOUR, Constants.FIRST_WORK_START);			
		}
		else
		{
			a.set(Constants.Fields.WORK_HOUR, Constants.SECOND_WORK_START);
		}
		a.set("Wander", new Text("No"));
		return a;
	}
	
	private static Agent setWanderTime(Agent a)
	{
		a.set(Constants.Fields.CLASS_HOUR, Constants.DEFAULT_HOUR);			
		a.set(Constants.Fields.LUNCH_HOUR, Constants.DEFAULT_HOUR);
		a.set(Constants.Fields.DINNER_HOUR, Constants.DEFAULT_HOUR);
		a.set(Constants.Fields.WORK_HOUR, Constants.DEFAULT_HOUR);
		a.set("Wander", new Text("Yes"));
		return a;
	}

	/**
	 * Create a number of random agents.
	 * 
	 * @param amount the amount of agents to create
	 * @param world the world where the agents will dwell
	 * @return an ArrayList with the created agents
	 */
	public static ArrayList<Agent> createRandomPopulation(
			final int amount, final World world) {
		ArrayList<Agent> population = new ArrayList<Agent>(amount);
		for (int i = 0; i < amount; i++) {
			population.add(createAgent(world, i, false));
		}
		for (int i = 0; i < amount; i++){
			population.add(createAgent(world, i, true));			
		}
		return population;
	}

	/**
	 * Create a random info field for the agents. In this case, the field's
	 * empty.
	 * 
	 * @param world the world the agent lives in
	 * @return the info field
	 */
	public static Map<String, Publishable> createRandomInfo(
			final World world) {
		Map<String, Publishable> info = new HashMap<String, Publishable>();
		return info;
	}

}
