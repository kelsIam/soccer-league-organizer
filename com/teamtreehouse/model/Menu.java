package com.teamtreehouse.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Menu {
    private BufferedReader mReader;
    private Map<String, String> mMenu;
    private Player[] mPlayers;
    private List<Team> mTeams;

    public Menu(Player[] players) {
        mPlayers = players;
        mReader= new BufferedReader(new InputStreamReader(System.in));
        mMenu = new HashMap<>();
        mTeams = new ArrayList<>();
        // add a menu option for creating a new team
        mMenu.put("create", "Create a new team");
        mMenu.put("add", "Add a player to a team");
        mMenu.put("remove", "Remove a player from a team");
        mMenu.put("report", "View a report of a team by height");
        mMenu.put("balance", "View the League Balance Report");
        mMenu.put("roster", "View roster");
    }

    public void run() {
        String choice = "";
        do {
            try {
                choice = promptAction();
                switch (choice) {
                    case "create":
                        Team team = promptNewTeam();
                        mTeams.add(team);
                        Collections.sort(mTeams);
                        System.out.printf("Team %s coached by %s added %n%n",
                                team.getTeamName(),
                                team.getCoachName());
                        break;
                    case "add":
                        Team chosenTeam =promptTeams();
                        Arrays.sort(mPlayers);
                        Player chosenPlayer = promptPlayers();
                        boolean added = chosenTeam.addPlayer(chosenPlayer);
                        if (added) {
                            System.out.printf("%s %s added to %s%n",
                                    chosenPlayer.getFirstName(),
                                    chosenPlayer.getLastName(),
                                    chosenTeam.getTeamName());
                        } else {
                            System.out.printf("Could not add player: team is full or player is already on this team.%n%n");
                        }
                        break;
                    case "remove":
                        Team rTeam = promptTeams();
                        Player rPlayers = promptTeamPlayers(rTeam);
                        boolean removed = rTeam.removePlayer(rPlayers);
                        if (removed) {
                            System.out.printf("%s %s removed from %s%n",
                                    rPlayers.getFirstName(),
                                    rPlayers.getLastName(),
                                    rTeam.getTeamName());
                        } else {
                            System.out.printf("Could not remove player: player is not on this team.%n%n%n");
                        }
                        break;
                    case "report":
                        Team hTeam = promptTeams();
                        Map<String, List<Player>> heightMap = byHeight(hTeam);
                        System.out.printf("Height report for %s:%n", hTeam.getTeamName());
                        for (Map.Entry<String, List<Player>> entry : heightMap.entrySet()) {
                            System.out.printf("%s%n", entry.getKey());
                            for (Player player : entry.getValue()) {
                                System.out.printf("  %s%n", player);
                            }
                        }
                        break;
                    case "balance":
                        for (Map.Entry<Team, int[]> entry : balanceReport().entrySet()) {
                            Team bTeam = entry.getKey();
                            int[] counts = entry.getValue();
                            System.out.printf("Team %s coached by %s: %d experienced, %d inexperienced%n",
                                    bTeam.getTeamName(), bTeam.getCoachName(), counts[0], counts[1]);
                        }
                        break;
                    case "roster":
                        Team rosTeam = promptTeams();
                        promptRoster(rosTeam);
                        break;
                    default:
                        System.out.printf("Unknown choice: '%s'. Try again. %n%n%n", choice);
                }
            } catch (IOException ioe) {
                System.out.println("Problem with input");
                ioe.printStackTrace();
            }
        } while (!choice.equals("Quit"));
    }

    //prompt to display all players in a selected team (roster)
    private void promptRoster (Team team) {
        System.out.printf("Roster for team %s: %n", team.getTeamName());
        for (Player player : team.getPlayers()) {
            System.out.printf(" %s %s (%d inches - %b)%n",
                    player.getFirstName(),
                    player.getLastName(),
                    player.getHeightInInches(),
                    player.isPreviousExperience()
            );
        }
    }

    private Map<Team, int[]> balanceReport() {
        Map <Team, int[]> balanceReport = new HashMap<>();
        //iterate through all teams, counting experienced and inexperienced players
        for (Team team : mTeams) {
            //create experienced and inexperienced vars
            int eCount = 0;
            int iCount = 0;
            //loop through players on the specific team
            for (Player player : team.getPlayers()) {
                //check if player experience is true or false
                //update count var accordingly
                if (player.isPreviousExperience()) {
                    eCount++;
                } else {
                    iCount++;
                }
            }
            //once done with a team, use a map to store these counts for each team
            balanceReport.put(team, new int[] {eCount, iCount});
        }
        //return map
        return balanceReport;
    }

    private Map<String, List<Player>> byHeight(Team team) {
        Map<String, List<Player>> byHeightRange = new TreeMap<>();
        //loop through team.getPlayers(), which is the players set for a specific team
        for (Player player : team.getPlayers()) {
            //assign rangeLabels: A, B, C, D using if/else
            String rangeLabel = "";
            if (player.getHeightInInches() < 35){
                rangeLabel = "A (less than 35 inches): ";
            } else if (player.getHeightInInches() <= 39) {
                rangeLabel = "B (35-39 inches): ";
            } else if (player.getHeightInInches() <= 45) {
                rangeLabel = "C (40-45 inches): ";
            } else if (player.getHeightInInches() > 45) {
                rangeLabel = "D (more than 45 inches): ";
            }
            //place player according to rangeLabel, need to check if there is an arraylist first
            List<Player> playersInRange = byHeightRange.get(rangeLabel);
            if (playersInRange == null) {
                playersInRange = new ArrayList<>();
                //byHeightRange is the map (main list)
                byHeightRange.put(rangeLabel, playersInRange);
            }
            //add player if list already exists
            playersInRange.add(player);
        }
        return byHeightRange;
    }

    private String promptAction() throws IOException {
        System.out.printf("Menu%n");
        //loop through menu
        for (Map.Entry<String, String> option : mMenu.entrySet()) {
            System.out.printf("%s - %s %n", option.getKey(), option.getValue());
        }
        System.out.print("Select an option: ");
        String choice = mReader.readLine();
        return choice.trim().toLowerCase();
    }

    //prompt for team name, coach name, print out "Team %s coached by %s added"
    private Team promptNewTeam() throws IOException {
        System.out.print("What is the team name? ");
        String teamName = mReader.readLine();
        System.out.print("What is the coach name? ");
        String coachName = mReader.readLine();
        return new Team(teamName, coachName);
    }

    //prompt for list of all available teams
    private Team promptTeams() throws IOException {
        System.out.printf("Available Teams: %n");
        List<String> teams = new ArrayList<>();
        for (Team team : mTeams) {
            teams.add(team.getTeamName());
        }
        int index = promptForIndex(teams);
        return mTeams.get(index);
    }

    //prompt to get all available players
    private Player promptPlayers() throws IOException {
        Set<Player> assignedPlayers = new HashSet<>();
        for (Team team : mTeams) {
            assignedPlayers.addAll(team.getPlayers());
        }
        List<Player> availablePlayers = new ArrayList<>();
        for (Player player : mPlayers) {
            if (!assignedPlayers.contains(player)) {
                availablePlayers.add(player);
            }
        }
        System.out.printf("Available Players: %n");
        List<String> displayList = new ArrayList<>();
        for (Player player : availablePlayers) {
            displayList.add(String.format(" %s %s (%d inches - %b)",
                    player.getFirstName(), player.getLastName(),
                    player.getHeightInInches(), player.isPreviousExperience()));
        }
        int index = promptForIndex(displayList);
        return availablePlayers.get(index);
    }

    //prompt to display all players in a selected team to remove
    private Player promptTeamPlayers(Team team) throws IOException {
        System.out.printf("Available Players on team %s: %n", team.getTeamName());
        //like promptArtist in karaoke machine
        List<Player> teamPlayers = new ArrayList<>(team.getPlayers());
        //create displayList because promptForIndex requires List<String> and Player is a Set
        List<String> displayList = new ArrayList<>();
        for (Player player : teamPlayers) {
            displayList.add(String.format(" %s %s (%d inches - %b)",
                    player.getFirstName(),
                    player.getLastName(),
                    player.getHeightInInches(),
                    player.isPreviousExperience()
            ));
        }
        int index = promptForIndex(displayList);
        return teamPlayers.get(index);
    }

//display a list of numbered items and then have someone choose an item from that list
    private int promptForIndex(List<String> options) throws IOException {
        int counter = 1;
        for (String option : options) {
            System.out.printf("%d.) %s%n", counter, option);
            counter++;
        }
        System.out.print("Your choice: ");
        String optionAsString = mReader.readLine();
        int choice = Integer.parseInt(optionAsString.trim());
        return choice - 1;
    }

}