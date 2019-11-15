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

import static de.nec.nle.siafu.ufjf.Constants.POPULATION;
import static de.nec.nle.siafu.ufjf.Constants.Fields.ACTIVITY;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

import de.nec.nle.siafu.behaviormodels.BaseAgentModel;
import de.nec.nle.siafu.exceptions.PlaceNotFoundException;
import de.nec.nle.siafu.model.Agent;
import de.nec.nle.siafu.model.World;
import de.nec.nle.siafu.types.EasyTime;
import de.nec.nle.siafu.types.Text;
import de.nec.nle.siafu.types.TimePeriod;
import de.nec.nle.siafu.ufjf.Constants;
import de.nec.nle.siafu.ufjf.Constants.Activity;

/**
 * This Agent Model defines the behavior of users in this test simulation.
 * Essentially, most users will wander around in a zombie like state, except
 * for Pietro and Teresa, who will stay put, and the postman, who will spend a
 * simulation life time running between the two ends of the map.
 * 
 * @author Miquel Martin
 * 
 */
public class AgentModel extends BaseAgentModel {
	
	private static Random rand = new Random();

	/**
	 * Constructor for the agent model.
	 * 
	 * @param world the simulation's world
	 */
	public AgentModel(final World world) {
		super(world);
	}

	/**
	 * Create a bunch of agents that will wander around aimlessly. Tweak them
	 * for testing purposes as needed. Two agents, Pietro and Teresa, are
	 * singled out and left under the control of the user. A third agent,
	 * Postman, is set to run errands between the two places int he map.
	 * 
	 * @return the created agents
	 */
	public ArrayList<Agent> createAgents() {
		ArrayList<Agent> people = AgentGenerator.createRandomPopulation(POPULATION, world);
		return people;
	}

	/**
	 * Make all the normal agents wander around, and the postman, run errands
	 * from one place to another. His speed depends on the time, slowing down
	 * at night.
	 * 
	 * @param agents the list of agents
	 */
	public void doIteration(final Collection<Agent> agents) {
		Calendar time = world.getTime();
		EasyTime now =
				new EasyTime(time.get(Calendar.HOUR_OF_DAY), time
						.get(Calendar.MINUTE));
		
		Iterator<Agent> peopleIt = agents.iterator();
		while (peopleIt.hasNext()) {
			try {
				Agent a = peopleIt.next();
				handlePerson(a, now);
			}
			catch (Exception e) {
				// TODO: handle exception
			}
		}

	}

	/**
	 * Keep the agent wandering around zombie style.
	 * 
	 * @param a the agent to zombiefy
	 * @throws PlaceNotFoundException 
	 */
	private void handlePerson(final Agent a, EasyTime now) throws PlaceNotFoundException {
		if (now.isIn(new TimePeriod(new EasyTime(23,30), new EasyTime(23,59))))
		{
			if (a.isVisible())
			{
				a.setPos(world.getPlacesOfType(a.get("InitialPlace").toString()).iterator().next().getPos());
				a.setVisible(false);
				a.setDestination(world.getPlacesOfType(a.get("InitialPlace").toString()).iterator().next());
				a.set(ACTIVITY, Constants.Activity.WAITING);
			}
			if (a.getImage().toString().equals("HumanBrown"))
			{
				a.set("Wander", new Text("Yes"));
				int hourWander = rand.nextInt(24);
				while(hourWander < 7 || hourWander > 22)
				{
					hourWander = rand.nextInt(24);
				}
				a.set("HourWander", new EasyTime(hourWander, 0));
			}
		}
		switch ((Activity) a.get(ACTIVITY)) {
		case WAITING:
			if (a.get(Constants.Fields.CLASS_HOUR) != Constants.DEFAULT_HOUR && a.get("Wander").toString().equals("No"))
			{
				if (a.get(Constants.Fields.CLASS_HOUR) == Constants.FIRST_CLASS_START)
				{
					Integer timeLate = rand.nextInt(30) + Constants.AGENT_WALKING_BLUR;
					EasyTime tp = new EasyTime(0,-timeLate);
					EasyTime tp2 = new EasyTime((EasyTime)a.get(Constants.Fields.CLASS_HOUR)).shift(tp);
					if (now.isIn(new TimePeriod(tp2, (EasyTime)a.get(Constants.Fields.CLASS_HOUR))))
					{
						a.setVisible(true);
						Integer placeId = rand.nextInt(16);
						String placeToGo = Constants.placesToStudy.get(placeId);
						a.setDestination(world.getPlacesOfType(placeToGo).iterator().next());
						a.set(ACTIVITY, Constants.Activity.STUDYING);
					}
				}
				else if (a.get(Constants.Fields.CLASS_HOUR) == Constants.SECOND_CLASS_START)
				{
					EasyTime tp = new EasyTime(Constants.LUNCH);
					EasyTime tp2 = new EasyTime(Constants.LUNCH_END);
					if (now.isIn(new TimePeriod(tp, tp2)) && a.get(ACTIVITY).toString() != "Eating")
					{
						a.setVisible(true);
						a.setDestination(world.getPlacesOfType("RestauranteUniversitario").iterator().next());
						a.set(ACTIVITY, Constants.Activity.EATING);
					}
				}
				else if (a.get(Constants.Fields.CLASS_HOUR) == Constants.THIRD_CLASS_START)
				{
					EasyTime tp = new EasyTime(Constants.DINNER);
					EasyTime tp2 = new EasyTime(Constants.DINNER_END);
					if (now.isIn(new TimePeriod(tp, tp2)) && a.get(ACTIVITY).toString() != "Eating")
					{
						a.setVisible(true);
						a.setDestination(world.getPlacesOfType("RestauranteUniversitario").iterator().next());
						a.set(ACTIVITY, Constants.Activity.EATING);
					}
				}
			}
			else if (a.get(Constants.Fields.WORK_HOUR) != Constants.DEFAULT_HOUR && a.get("Wander").toString().equals("No"))
			{
				EasyTime tp = new EasyTime(0,-45);
				EasyTime tp2 = new EasyTime((EasyTime)a.get(Constants.Fields.WORK_HOUR)).shift(tp);
				if (now.isIn(new TimePeriod(tp2, (EasyTime)a.get(Constants.Fields.WORK_HOUR))))
				{
					a.setVisible(true);
					Integer placeId = rand.nextInt(4);
					String placeToGo = Constants.placesToWork.get(placeId);
					a.setDestination(world.getPlacesOfType(placeToGo).iterator().next());
					a.set(ACTIVITY, Constants.Activity.WORKING);
				} 
			}
			else if (a.get("Wander").toString().equals("Yes"))
			{
				if (!a.get("HourWander").toString().equals("None"))
				{
					if (now.isAfter(new EasyTime(a.get("HourWander").toString())))
					{
						a.setVisible(true);
						a.setSpeed(5);
						a.set("HourWander", new Text("None"));
						if(a.get("InitialPlace").toString().equals("PortaoNorte"))
						{
							a.setDestination(world.getPlacesOfType("PortaoSul").iterator().next());
						}
						else
						{
							a.setDestination(world.getPlacesOfType("PortaoNorte").iterator().next());							
						}
					}
				}
				else
				{
					if(a.getPos().equals(world.getPlacesOfType("PortaoNorte").iterator().next().getPos()) && !a.get("InitialPlace").toString().equals("PortaoNorte"))
					{
						a.setVisible(false);
						a.moveTowardsPlace(world.getPlacesOfType(a.get("InitialPlace").toString()).iterator().next());
						a.set("Wander", new Text("No"));
					}
					else if(a.getPos().equals(world.getPlacesOfType("PortaoSul").iterator().next().getPos()) && !a.get("InitialPlace").toString().equals("PortaoSul"))
					{
						a.setVisible(false);
						a.moveTowardsPlace(world.getPlacesOfType(a.get("InitialPlace").toString()).iterator().next());
						a.set("Wander", new Text("No"));						
					}
				}
			}
			break;
		case STUDYING:
			if ((EasyTime) a.get(Constants.Fields.CLASS_HOUR) == (EasyTime)Constants.FIRST_CLASS_START)
			{
				Integer timeLate = rand.nextInt(60) - Constants.AGENT_STUDYING_BLUR;
				EasyTime tp = new EasyTime(0, timeLate);
				EasyTime tp2 = new EasyTime(Constants.FIRST_CLASS_END); 
				tp2.shift(tp);
				if (now.isAfter(tp2))
				{
					a.setDestination(world.getPlacesOfType("RestauranteUniversitario").iterator().next());
					a.set(ACTIVITY, Constants.Activity.EATING);
				}
			}
			else if ((EasyTime) a.get(Constants.Fields.CLASS_HOUR) == Constants.SECOND_CLASS_START)
			{
				Integer timeLate = rand.nextInt(60) - Constants.AGENT_STUDYING_BLUR;
				EasyTime tp = new EasyTime(0, timeLate);
				EasyTime tp2 = new EasyTime(Constants.SECOND_CLASS_END);
				tp2.shift(tp);
				if (now.isAfter(tp2))
				{
					a.setDestination(world.getPlacesOfType("RestauranteUniversitario").iterator().next());
					a.set(ACTIVITY, Constants.Activity.EATING);
				}				
			}
			else if ((EasyTime) a.get(Constants.Fields.CLASS_HOUR) == Constants.THIRD_CLASS_START)
			{
				Integer timeLate = rand.nextInt(60) - Constants.AGENT_STUDYING_BLUR;
				EasyTime tp = new EasyTime(0, timeLate);
				EasyTime tp2 = new EasyTime(Constants.THIRD_CLASS_END);
				tp2.shift(tp);
				if (now.isAfter(tp2))
				{
					a.setDestination(world.getPlacesOfType(a.get("InitialPlace").toString()).iterator().next());
					a.set(ACTIVITY, Constants.Activity.WALKING);
					a.set("TemporaryDestination", new Text(a.get("InitialPlace").toString()));
				}				
			}
			break;
		case EATING:
			if (a.get(Constants.Fields.CLASS_HOUR) == Constants.FIRST_CLASS_START)
			{
				Integer timeLate = rand.nextInt(Constants.AGENT_EATING_BLUR);
				EasyTime tp = new EasyTime(0,-timeLate);
				EasyTime tp2 = new EasyTime(Constants.LUNCH_END).shift(tp);
				if (now.isAfter(tp2))
				{
					a.setDestination(world.getPlacesOfType(a.get("InitialPlace").toString()).iterator().next());
					a.set(ACTIVITY, Constants.Activity.WALKING);
					a.set("TemporaryDestination", new Text(a.get("InitialPlace").toString()));
				}
			}			
			else if (a.get(Constants.Fields.CLASS_HOUR) == Constants.SECOND_CLASS_START && now.isAfter(new EasyTime(12, 30)))
			{
				if (now.isBefore(Constants.DINNER))
				{
					Integer timeLate = rand.nextInt(30) + Constants.AGENT_WALKING_BLUR;
					EasyTime tp = new EasyTime(0,-timeLate);
					EasyTime tp2 = new EasyTime((EasyTime)a.get(Constants.Fields.CLASS_HOUR)).shift(tp);
					if (now.isIn(new TimePeriod(tp2, (EasyTime)a.get(Constants.Fields.CLASS_HOUR))))
					{
						Integer placeId = rand.nextInt(16);
						String placeToGo = Constants.placesToStudy.get(placeId);
						a.setDestination(world.getPlacesOfType(placeToGo).iterator().next());
						a.set(ACTIVITY, Constants.Activity.STUDYING);
					}
				}
				else if (now.isAfter(Constants.DINNER))
				{
					Integer timeLate = rand.nextInt(30) + Constants.AGENT_WALKING_BLUR;
					EasyTime tp = new EasyTime(0,-timeLate);
					EasyTime tp2 = new EasyTime(Constants.DINNER_END).shift(tp);
					if (now.isAfter(tp2))
					{
						a.setDestination(world.getPlacesOfType(a.get("InitialPlace").toString()).iterator().next());
						a.set(ACTIVITY, Constants.Activity.WALKING);
						a.set("TemporaryDestination", new Text(a.get("InitialPlace").toString()));
					}
				}
			}
			else if (a.get(Constants.Fields.CLASS_HOUR) == Constants.THIRD_CLASS_START && now.isAfter(new EasyTime(18, 00)))
			{
				Integer timeLate = rand.nextInt(30) + Constants.AGENT_WALKING_BLUR;
				EasyTime tp = new EasyTime(0,-timeLate);
				EasyTime tp2 = new EasyTime((EasyTime)a.get(Constants.Fields.CLASS_HOUR)).shift(tp);
				if (now.isIn(new TimePeriod(tp2, (EasyTime)a.get(Constants.Fields.CLASS_HOUR))))
				{
					Integer placeId = rand.nextInt(16);
					String placeToGo = Constants.placesToStudy.get(placeId);
					a.setDestination(world.getPlacesOfType(placeToGo).iterator().next());
					a.set(ACTIVITY, Constants.Activity.STUDYING);
				}								
			}			
			break;
		case WORKING:
			if ((EasyTime) a.get(Constants.Fields.WORK_HOUR) == Constants.FIRST_WORK_START)
			{
				if (now.isAfter(Constants.FIRST_WORK_END))
				{
					a.setDestination(world.getPlacesOfType(a.get("InitialPlace").toString()).iterator().next());
					a.set(ACTIVITY, Constants.Activity.WALKING);
					a.set("TemporaryDestination", new Text(a.get("InitialPlace").toString()));
				}
			}
			else if ((EasyTime) a.get(Constants.Fields.WORK_HOUR) == Constants.SECOND_WORK_START)
			{
				if (now.isAfter(Constants.SECOND_WORK_END))
				{
					a.setDestination(world.getPlacesOfType(a.get("InitialPlace").toString()).iterator().next());
					a.set(ACTIVITY, Constants.Activity.WALKING);
					a.set("TemporaryDestination", new Text(a.get("InitialPlace").toString()));
				}				
			}
			break;
		case WALKING:
			if (a.getPos().equals(world.getPlacesOfType(a.get("InitialPlace").toString()).iterator().next().getPos()))
			{
				a.setVisible(false);
				a.set(ACTIVITY, Constants.Activity.WAITING);
				a.set("TemporaryDestination", new Text("None"));
			}
			break;
		default:
			throw new RuntimeException("Unable to handle activity "
					+ (Activity) a.get(ACTIVITY));
		}

	}	
}
