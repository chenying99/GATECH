package nfa;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Utilities for analyzing and converting NFA's.
 * 
 * @author toriscope
 * 
 */
public class NFAUtil {

	private NFAUtil() {
		;
	}

	public static NFA convertToDFA(final NFA nfaInit, final char[] totalLexicon) {
		/*
		 * Each DfaConStep[] will feature a closure for every transition
		 * character.
		 */
		List<MetaState> remainingClosures = new LinkedList<MetaState>();
		remainingClosures.add(new MetaState(findClosure(nfaInit.getStartState())));

		while (remainingClosures.isEmpty()) {

			MetaState metaState = remainingClosures.get(0);

			// Moves per transition
			HashMap<String, Set<State>> transTo = new HashMap<String, Set<State>>();
			for (State state : metaState.states) {
				for (Transition trans : state.getTransitions()) {
					// For this trans, place the state in the the transMap
					if (!trans.isEmptyTransition()) {
						if (!transTo.containsKey(trans.getRegex())) {
							transTo.put(trans.getRegex(), new HashSet<State>());
						}
						transTo.get(trans.getRegex()).add(trans.getDestinationState());
					}
				}
			}

			// Find the epsilon enclosures for each
			HashMap<String, Set<State>> epsilons = new HashMap<String, Set<State>>();
			for (Entry<String, Set<State>> e : transTo.entrySet()) {
				// e.getKey()
			}

		}

		return null;
	}

	/**
	 * Returns all states within E^* of given state.
	 * 
	 * @param state
	 *            state to find full closure of.
	 * @return all states that can be reached in zero or more E-Closures from
	 *         given state.
	 */
	public static List<State> findClosure(final State state) {

		final HashSet<State> explored = new HashSet<State>();
		final HashSet<State> frontier = new HashSet<State>();
		frontier.add(state);

		while (!frontier.isEmpty()) {
			State c = frontier.iterator().next();
			for (Transition t : c.getTransitions()) {
				if (t.isEmptyTransition() && !frontier.contains(t.getDestinationState())
						&& !explored.contains(t.getDestinationState())) {
					frontier.add(t.getDestinationState());
				}
			}
			frontier.remove(c);
			explored.add(c);
		}

		return new LinkedList<State>(explored);

	}

	public static NFA minimizeDFA(final NFA nfaInit, final char[] totalLexicon) {

		// Run the minimization algorithm

		return null;
	}

	public static boolean isValid(final NFASegment nfa, final String string) {
		return isValid(new NFA(nfa.start), string);
	}

	/**
	 * Check if the string is valid in the NFA.
	 * 
	 * @param nfa
	 *            the nfa to check the string against
	 * @param string
	 *            string to check for validity
	 * @return true if the string is valid, false otherwise.
	 */
	public static boolean isValid(final NFA nfa, final String string) {

		final HashSet<NFAStep> steps = new HashSet<NFAStep>();
		final HashSet<NFAStep> explored = new HashSet<NFAStep>();

		boolean isValid = false;

		steps.add(new NFAStep(nfa.getStartState(), string));

		while (!(isValid || steps.isEmpty())) {
			NFAStep step = steps.iterator().next();
			explored.add(step);

			steps.remove(step);

			// Check for finality
			if (step.isFinal()) {
				isValid = true;
			}

			for (Transition t : step.state.getTransitions()) {
				NFAStep s = null;
				// If empty
				if (t.isEmptyTransition()) {
					s = new NFAStep(t.getDestinationState(), step.string);
				}
				// if not empty, and string is not empty
				else if (!step.string.isEmpty() && t.isValid(step.string.charAt(0))) {
					s = new NFAStep(t.getDestinationState(), step.string.substring(1));
				}

				if (s != null && !explored.contains(s)) {
					steps.add(s);
				}
			}
		}

		return isValid;
	}

	/**
	 * Execution step in an NFA walk.
	 * 
	 * @author toriscope
	 * 
	 */
	private static class NFAStep {
		private final State state;
		private final String string;

		public NFAStep(final State state, final String string) {
			this.state = state;
			this.string = string;
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof NFAStep) {
				NFAStep p = (NFAStep) o;
				return p.state == this.state && p.string.equals(this.string);
			} else {
				System.err.println("Object is not an instance of NFAStep");
				return false;
			}
		}

		@Override
		public int hashCode() {
			return state.hashCode() * string.hashCode();
		}

		public String toString() {
			return state.toString() + " " + string;
		}

		public boolean isFinal() {
			return string.isEmpty() && state.isFinal();
		}
	}

	/**
	 * Single meta-state entry in E-Closure table
	 * 
	 * @author toriscope
	 * 
	 */
	private static class MetaState {
		private final String name;
		private final List<State> states;
		private final List<MetaState> transitionTo;
		private boolean expanded;

		public static int unique = 0;

		public MetaState(List<State> states) {
			this.states = states;
			this.name = "" + unique++;
			transitionTo = new LinkedList();
			expanded = false;

		}

		public void addTransition(MetaState s) {
			transitionTo.add(s);
		}

		@Override
		public int hashCode() {
			int ohgeez = 1;
			for (State s : states) {
				ohgeez *= s.hashCode();
			}
			return ohgeez;
		}

		@Override
		public boolean equals(final Object o) {
			if (o instanceof MetaState) {
				MetaState s = (MetaState) o;
				if (s.states.size() != this.states.size()) {
					return false;
				}
				for (State state : s.states) {
					if (!this.states.contains(state)) {
						return false;
					}
				}
			} else
				return false;

			return true;
		}
	}

	// Create unique names
	private static int gen = 0;

	public static NFASegment a(final String regex) {
		State start = new State("start" + gen, false);
		State end = new State("end" + gen++, false);

		start.addTransition(new Transition(regex, end));

		return new NFASegment(start, end);
	}

	public static NFASegment aOrB(final NFASegment... segments) {
		State start = new State("start" + gen, false);
		State end = new State("end" + gen++, false);

		if (segments.length < 1) {
			start.addTransition(new Transition(end));
		}

		for (NFASegment s : segments) {
			start.addTransition(new Transition(s.start));
			s.end.addTransition(new Transition(end));
		}

		return new NFASegment(start, end);
	}

	public static NFASegment aStar(final NFASegment a) {
		State start = new State("start" + gen, false);
		State end = new State("end" + gen++, false);

		start.addTransition(new Transition(a.start));
		start.addTransition(new Transition(end));

		a.end.addTransition(new Transition(end));
		end.addTransition(new Transition(start));

		return new NFASegment(start, end);
	}

	public static NFASegment aPlus(final NFASegment a) {
		State start = new State("start" + gen, false);
		State end = new State("end" + gen++, false);

		start.addTransition(new Transition(a.start));

		a.end.addTransition(new Transition(end));
		end.addTransition(new Transition(start));

		return new NFASegment(start, end);
	}

	public static NFASegment ab(final NFASegment a, final NFASegment b) {
		a.end.addTransition(new Transition(b.start));
		return new NFASegment(a.start, b.end);
	}

	public static class NFASegment {
		public final State start, end;

		public NFASegment(final State start, final State end) {
			this.start = start;
			this.end = end;
		}
	}
}
