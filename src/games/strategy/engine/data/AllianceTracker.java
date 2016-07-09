package games.strategy.engine.data;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Tracks alliances between players.
 * An alliance is a named entity, players are added to an alliance.
 * Currently only used for tracking stats (like TUV, total production, etc), and for tracking total victory cities for
 * alliance based
 * victory conditions.
 * Not used for determining in-game alliances (instead, see the Relationship tracker for that).
 */
public class AllianceTracker extends GameDataComponent {
  private static final long serialVersionUID = 2815023984535209353L;
  // maps PlayerID to Collection of alliances names
  private Multimap<PlayerID, String> alliances = HashMultimap.create();

  public AllianceTracker(final GameData data) {
    super(data);
  }

  /**
   * Adds PlayerID player to the alliance specified by allianceName.
   *
   * @param player       The player to add to the alliance.
   * @param allianceName The alliance to add to.
   */
  protected void addToAlliance(final PlayerID player, final String allianceName) {
    alliances.put(player, allianceName);
  }

  /**
   * @return a set of all the games alliances, this will return an empty set if you aren't using alliances
   */
  public Set<String> getAlliances() {
    final Iterator<PlayerID> keys = alliances.keySet().iterator();
    final Set<String> rVal = new HashSet<>();
    while (keys.hasNext()) {
      rVal.addAll(alliances.get(keys.next()));
    }
    return rVal;
  }

  /**
   * Returns the PlayerID's that are members of the alliance
   * specified by the String allianceName
   *
   * @param allianceName Alliance name
   * @return all the players in the given alliance
   */
  public HashSet<PlayerID> getPlayersInAlliance(final String allianceName) {
    final Iterator<PlayerID> keys = alliances.keySet().iterator();
    final HashSet<PlayerID> rVal = new HashSet<>();
    while (keys.hasNext()) {
      final PlayerID player = keys.next();
      final Collection<String> alliances = this.alliances.get(player);
      if (alliances.contains(allianceName)) {
        rVal.add(player);
      }
    }
    return rVal;
  }

  public Collection<String> getAlliancesPlayerIsIn(final PlayerID player) {
    final Collection<String> rVal = alliances.get(player);
    if (rVal == null) {
      System.out.println("Player, " + player.getName() + ", is not a member of any alliance!");
      return Collections.singleton(player.getName());
    }
    return rVal;
  }

  public Multimap<PlayerID, String> getAlliancesMap() {
    return alliances;
  }

  public Set<PlayerID> getAllies(PlayerID currentPlayer) {
    Set<PlayerID> allies = new HashSet<>();
    // iterate through all alliances the player is in
    if (getAlliancesMap().get(currentPlayer) != null) {
      for (final String alliance : getAlliancesMap().get(currentPlayer)) {
        // iterate through the members of the alliances
        for (final PlayerID alliedPlayer : getPlayersInAlliance(alliance)) {
          // add each allianceMember to the alliesList
          allies.add(alliedPlayer);
        }
      }
    }
    return allies;
  }
}
