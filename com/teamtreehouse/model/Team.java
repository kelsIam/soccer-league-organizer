package com.teamtreehouse.model;

import java.util.TreeSet;
import java.util.Set;

public class Team implements Comparable<Team>{
    protected String teamName;
    protected String coachName;
    protected Set<Player> players;

    //constructor
    public Team(String teamName, String coachName) {
        this.teamName = teamName;
        this.coachName = coachName;
        this.players = new TreeSet<>(); //why a HashSet?
    }

    //getters
    public String getTeamName() {
        return teamName;
    }

    public String getCoachName() {
        return coachName;
    }

    public Set<Player> getPlayers() {
        return players;
    }

    public boolean addPlayer(Player player) {
        //check if the team has more than 11 players
        if(players.size() >= 11) {
            return false;
        }
        return players.add(player);
    }

    //remove players from selected team
    public boolean removePlayer(Player player) {
        if (player == null) {
            return false;
        }
        return players.remove(player);
    }

    @Override
    public String toString() {
        return "Team{" +
                "teamName='" + teamName + '\'' +
                ", coachName='" + coachName + '\'' +
                '}';
    }

    @Override
    public int compareTo(Team other) {
        return this.getTeamName().compareTo(other.getTeamName());
    }
}
