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

import java.util.LinkedList;
import java.util.List;

import de.nec.nle.siafu.types.EasyTime;
import de.nec.nle.siafu.types.FlatData;
import de.nec.nle.siafu.types.Publishable;
import de.nec.nle.siafu.types.Text;

/**
 * A list of the constants used by this simulation. None of this is strictly
 * needed, but it makes referring to certain values easier and less error prone.
 * 
 * @author Miquel Martin
 */
public class Constants {
	/**
	 * Population size, that is, how many agents should inhabit this simulation.
	 */
	@SuppressWarnings("serial")
	public static List<String> placesToStudy = new LinkedList<String>() {
    	{
    		add("CBR");
    		add("FaculdadeDeAdministracaoECienciasContabeis");
    		add("FaculdadeDeArquitetura");
    		add("FaculdadeDeComunicacao");
    		add("FaculdadeDeDireito");
    		add("FaculdadeDeEconomia");
    		add("FaculdadeDeEducacao");
    		add("FaculdadeDeEducacaoFisicaEDesportos");
    		add("FaculdadeDeEngenharia");
    		add("FaculdadeDeLetras");
    		add("InstitutoDeArtesEDesign");
    		add("InstitutoDeCienciasBiologicas");
    		add("InstitutoDeCienciasDaSaude");
    		add("InstitutoDeCienciasExatas");
    		add("InstitutoDeCienciasExatasAntigo");
    		add("InstitutoDeCienciasHumanas");
    	}
    };
    
	@SuppressWarnings("serial")
	public static List<String> placesToWork = new LinkedList<String>() {
    	{
    		add("CGCO");
    		add("CRITT");
    		add("ProReitoriaDeInfraestrutura");
    		add("Reitoria");
    	}	
    };
	
	public static final int POPULATION = 150;
	
	public static final int DEFAULT_SPEED = 8;
			
	public static final EasyTime FIRST_CLASS_START = new EasyTime(8, 0);
	
	public static final EasyTime FIRST_CLASS_END = new EasyTime(12, 0);
		
	public static final EasyTime SECOND_CLASS_START = new EasyTime(14,0);
	
	public static final EasyTime SECOND_CLASS_END = new EasyTime(18, 0);
	
	public static final EasyTime THIRD_CLASS_START = new EasyTime(19, 0);

	public static final EasyTime THIRD_CLASS_END = new EasyTime(23, 0);
	
	public static final EasyTime FIRST_WORK_START = new EasyTime(8, 0);
	
	public static final EasyTime FIRST_WORK_END = new EasyTime(14, 0);
	
	public static final EasyTime SECOND_WORK_START = new EasyTime(14, 0);
	
	public static final EasyTime SECOND_WORK_END = new EasyTime(22, 0);
	
	public static final EasyTime LUNCH = new EasyTime(11,0);
	
	public static final EasyTime LUNCH_END = new EasyTime(14,0);
	
	public static final EasyTime DINNER = new EasyTime(17,0);
	
	public static final EasyTime DINNER_END = new EasyTime(19,0);
		
	public static final int AGENT_WALKING_BLUR = 30;
	
	public static final int AGENT_STUDYING_BLUR = 30;
	
	public static final int AGENT_EATING_BLUR = 60;
	
	public static final EasyTime DEFAULT_HOUR = new EasyTime(0,0);

	/**
	 * The names of the fields in each agent object.
	 */
	static class Fields {
		/** The agent's current activity. */
		public static final String ACTIVITY = "Activity";
		
		public static final String CLASS_HOUR = "Class";
		
		public static final String WORK_HOUR = "Work";
		
		public static final String LUNCH_HOUR = "Lunch";
		
		public static final String DINNER_HOUR = "Dinner";
	}

	/* FIXME the activity doesn't show the actual description */
	/**
	 * List of possible activies. This is implemented as an enum because it
	 * helps us in switch statements. Like the rest of the constants in this
	 * class, they could also have been coded directly in the model
	 */
	enum Activity implements Publishable {
		/** The agent's waiting. */
		WAITING("Waiting"),
		
		STUDYING("Studying"),
		
		EATING("Eating"),
		
		WALKING("Walking"),
		
		WORKING("Working");

		/** Human readable desription of the activity. */
		private String description;

		/**
		 * Get the description of the activity.
		 * 
		 * @return a string describing the activity
		 */
		public String toString() {
			return description;
		}

		/**
		 * Build an instance of Activity which keeps a human readable
		 * description for when it's flattened.
		 * 
		 * @param description the humanreadable description of the activity
		 */
		private Activity(final String description) {
			this.description = description;
		}

		/**
		 * Flatten the description of the activity.
		 * 
		 * @return a flatenned text with the description of the activity
		 */
		public FlatData flatten() {
			return new Text(description).flatten();
		}
	}
}
