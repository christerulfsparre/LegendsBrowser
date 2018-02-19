package legends.helper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import legends.model.HistoricalFigure;
import legends.model.World;
import legends.model.collections.BattleCollection;
import legends.model.collections.WarCollection;
import legends.model.collections.basic.EventCollection;
import legends.model.events.HfDiedEvent;
import legends.model.events.basic.Event;

public class StatHelper {
    public static HashMap<String, Collection<HfDiedEvent>> getHistoricalFigureKills(HistoricalFigure hf) {
        return deathsSortedBy(World.getHistoricalEvents(), e -> {
            if(e.getSlayerHfId() == hf.getId()) {
                return World.getHistoricalFigure(e.getHfId()).getRace();
            }
            return null;
        });
    }

    public static HashMap<Integer, Collection<HfDiedEvent>> getAllHistoricalFigureKills() {
		return deathsSortedBy(World.getHistoricalEvents(), e -> e.getSlayerHfId() );
    }

    public static HashMap<Integer, Collection<HfDiedEvent>> getWarCollectionKills(List<EventCollection> collections) {
        HashMap<Integer, ArrayList<HfDiedEvent>> result = new HashMap<Integer, ArrayList<HfDiedEvent>>();
        collections.forEach(collection -> {
            getDiedEvents(getBattleCollections(collection)
            .stream()
            .map(co -> co.getHistoricalEvents())
            .flatMap(l -> l.stream())
            .collect(Collectors.toList())
            , event -> {
                Integer key = collection.getId();
                ArrayList<HfDiedEvent> events = result.get(key);
                if(events == null) {
                    events = new ArrayList<HfDiedEvent>();
                    result.put(key, events);
                }
                events.add(event);
            });
        });
        return new HashMap<Integer, Collection<HfDiedEvent>>(result);
    }

    public static HashMap<Integer, Collection<HfDiedEvent>> getBattleCollectionKills(List<EventCollection> collections) {
        HashMap<Integer, ArrayList<HfDiedEvent>> result = new HashMap<Integer, ArrayList<HfDiedEvent>>();
        collections.forEach(collection -> {
            getDiedEvents(collection.getHistoricalEvents(), event -> {
                Integer key = collection.getId();
                ArrayList<HfDiedEvent> events = result.get(key);
                if(events == null) {
                    events = new ArrayList<HfDiedEvent>();
                    result.put(key, events);
                }
                events.add(event);
            });
        });
        return new HashMap<Integer, Collection<HfDiedEvent>>(result);
    }

    private static Collection<BattleCollection> getBattleCollections(EventCollection collection) {
        ArrayList<BattleCollection> result = new ArrayList<BattleCollection>();
        if(collection instanceof BattleCollection) {
            result.add((BattleCollection)collection);
        }
        else if(collection instanceof WarCollection) {
            collection.getHistoricalEventCollections().forEach(c -> {
                if(c instanceof BattleCollection) {
                    result.add((BattleCollection)c);
                }
            });
        }
        return result;
    }

    /*
     * Takes a list of either Events or Ids to events.
     * 
     * Then sorts all HfDiedEvents in the list into a HashMap.
     */
    private static <T, K> HashMap<K, Collection<HfDiedEvent>> deathsSortedBy(Collection<T> collection, Function<HfDiedEvent, K> sortBy)
    {
        HashMap<K, ArrayList<HfDiedEvent>> result = new HashMap<K, ArrayList<HfDiedEvent>>();
        getDiedEvents(collection, event -> {
            K key = sortBy.apply(event);
            if(key != null) {
                ArrayList<HfDiedEvent> events = result.get(key);
                if(events == null) {
                    events = new ArrayList<HfDiedEvent>();
                    result.put(key, events);
                }
                events.add(event);
            }
        });
        return new HashMap<K, Collection<HfDiedEvent>>(result);
    }

    /*
     * Takes a list of either Events or Ids to events.
     * 
     * Interates through all events and calls the action foreach found HfDiedEvent.
     */
    private static <T> void getDiedEvents(Collection<T> list, Consumer<HfDiedEvent> action)
    {
        list.forEach(o -> {
            if(o instanceof HfDiedEvent) {
                action.accept((HfDiedEvent)o);
            }
            else if(o instanceof Integer) {
                Event e = World.getHistoricalEvent((Integer)o);
                if(e instanceof HfDiedEvent) {
                    action.accept((HfDiedEvent)e);
                }
            }
        });
    }
}