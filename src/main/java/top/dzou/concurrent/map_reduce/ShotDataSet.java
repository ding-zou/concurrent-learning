package top.dzou.concurrent.map_reduce;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author dingxiang
 * @date 19-8-12 下午8:55
 */
@Data
@AllArgsConstructor
public class ShotDataSet {
    private String game_id;
    private String matchUp;
    private String location;
    private String w;
    private String finalMargin;
    private String shot_number;
    private String period;
    private String gameClock;
    private String shotClock;
    private String dribbles;
    private String touchTime;
    private String shotDist;
    private String ptsType;
    private String shotRes;
    private String closestDefend;
    private String cDPId;
    private String cDDist;
    private String fgm;
    private String points;
    private String playName;
    private String playId;
}
