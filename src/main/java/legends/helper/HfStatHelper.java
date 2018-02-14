package legends.helper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import legends.model.HistoricalFigure;
import legends.model.World;
import legends.model.events.HfDiedEvent;

public class HfStatHelper {
    public static HashMap<String, Integer> getHistoricalFigureKills(HistoricalFigure hf) {
        HashMap<String, Integer> result = new HashMap<String, Integer>();
        ArrayList<HfDiedEvent> empty = new ArrayList<HfDiedEvent>();
        getAllHistoricalFigureKills().getOrDefault(hf.getId(), empty).forEach(e ->
        {
            String race = World.getHistoricalFigure(e.getHfId()).getRace();
            result.put(race, result.getOrDefault(race, 0) + 1);
        });
        return result;
    }

    /*
     * Returns all historical figure kills as a hash map.
     * 
     * Keys     = Historical figure id
     * Value    = Number of kills
     */
    public static HashMap<Integer, ArrayList<HfDiedEvent>> getAllHistoricalFigureKills() {
        HashMap<Integer, ArrayList<HfDiedEvent>> kills = new HashMap<>();
        World.getHistoricalEvents().forEach(e -> {
			if(e instanceof HfDiedEvent) {
                HfDiedEvent event = (HfDiedEvent)e;
                Integer id = event.getSlayerHfId();

                ArrayList<HfDiedEvent> events = kills.get(id);
                if(events == null) {
                    events = new ArrayList<HfDiedEvent>();
                    kills.put(id, events);
                }
                events.add(event);
			}
		});
		return kills;
	}
}