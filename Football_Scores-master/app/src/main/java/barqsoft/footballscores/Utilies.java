package barqsoft.footballscores;

import android.content.res.Resources;

/**
 * Created by yehya khaled on 3/3/2015.
 */
public class Utilies {

    public static final int SERIE_A = 357;
    public static final int PREMIER_LEAGUE = 354;
    public static final int CHAMPIONS_LEAGUE = 362;
    public static final int PRIMERA_DIVISION = 358;
    public static final int BUNDESLIGA = 351;


    public static String getLeague(int league_num) {
        Resources res = Resources.getSystem();

        switch (league_num) {
            case SERIE_A:
                return res.getString(R.string.uti_seria_a);
            case PREMIER_LEAGUE:
                return res.getString(R.string.uti_premier_league);
            case CHAMPIONS_LEAGUE:
                return res.getString(R.string.uti_uefa);
            case PRIMERA_DIVISION:
                return res.getString(R.string.uti_primera);
            case BUNDESLIGA:
                return res.getString(R.string.uti_bundes);
            default:
                return res.getString(R.string.uti_unk);
        }
    }

    public static String getMatchDay(int match_day, int league_num) {
        Resources res = Resources.getSystem();

        if (league_num == CHAMPIONS_LEAGUE) {
            if (match_day <= 6) {
                return res.getString(R.string.uti_md_group);
            } else if (match_day == 7 || match_day == 8) {
                return res.getString(R.string.uti_md_knock);
            } else if (match_day == 9 || match_day == 10) {
                return res.getString(R.string.uti_md_quarter);
            } else if (match_day == 11 || match_day == 12) {
                return res.getString(R.string.uti_md_semi);
            } else {
                return res.getString(R.string.uti_md_final);
            }
        } else {
            return res.getString(R.string.uti_md_match_day) + String.valueOf(match_day);
        }
    }

    public static String getScores(int home_goals, int awaygoals) {
        if (home_goals < 0 || awaygoals < 0) {
            return " - ";
        } else {
            return String.valueOf(home_goals) + " - " + String.valueOf(awaygoals);
        }
    }

    public static int getTeamCrestByTeamName(String teamname) {
        if (teamname == null) {
            return R.drawable.no_icon;
        }
        switch (teamname) { //This is the set of icons that are currently in the app. Feel free to find and add more
            //as you go.
            case "Arsenal London FC":
                return R.drawable.arsenal;
            case "Manchester United FC":
                return R.drawable.manchester_united;
            case "Swansea City":
                return R.drawable.swansea_city_afc;
            case "Leicester City":
                return R.drawable.leicester_city_fc_hd_logo;
            case "Everton FC":
                return R.drawable.everton_fc_logo1;
            case "West Ham United FC":
                return R.drawable.west_ham;
            case "Tottenham Hotspur FC":
                return R.drawable.tottenham_hotspur;
            case "West Bromwich Albion":
                return R.drawable.west_bromwich_albion_hd_logo;
            case "Sunderland AFC":
                return R.drawable.sunderland;
            case "Stoke City FC":
                return R.drawable.stoke_city;
            default:
                return R.drawable.no_icon;
        }
    }
}
