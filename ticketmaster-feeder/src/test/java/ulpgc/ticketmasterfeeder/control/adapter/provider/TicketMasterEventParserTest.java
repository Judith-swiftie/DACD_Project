package ulpgc.ticketmasterfeeder.control.adapter.provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ulpgc.ticketmasterfeeder.model.Artist;
import ulpgc.ticketmasterfeeder.model.Event;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class TicketMasterEventParserTest {
    private TicketMasterEventParser parser;

    @BeforeEach
    void setUp() {
        parser = new TicketMasterEventParser(new ObjectMapper());
    }

    @Test
    void parseEventsFromBody_shouldParseValidMusicEvent() throws Exception {
        String json = """
            {
              "_embedded": {
                "events": [
                  {
                    "name": "Concert Example",
                    "dates": {
                      "start": {
                        "localDate": "2025-06-20",
                        "localTime": "20:00:00"
                      }
                    },
                    "classifications": [
                      {
                        "segment": {
                          "name": "Music"
                        }
                      }
                    ],
                    "_embedded": {
                      "venues": [
                        {
                          "name": "Stadium",
                          "city": {"name": "Madrid"},
                          "country": {"name": "Spain"}
                        }
                      ],
                      "attractions": [
                        {"name": "Artist One"},
                        {"name": "Artist Two"}
                      ]
                    },
                    "priceRanges": [
                      {
                        "min": 50.0,
                        "max": 100.0,
                        "currency": "EUR"
                      }
                    ]
                  }
                ]
              }
            }
            """;

        List<Event> events = parser.parseEventsFromBody(json);
        assertNotNull(events);
        assertEquals(1, events.size());
        Event event = events.getFirst();
        assertEquals("Concert Example", event.getName());
        assertEquals("2025-06-20", event.getDate());
        assertEquals("20:00:00", event.getTime());
        assertEquals("Stadium", event.getVenue());
        assertEquals("Madrid", event.getCity());
        assertEquals("Spain", event.getCountry());
        List<Artist> artists = event.getArtists();
        assertEquals(2, artists.size());
        assertEquals("Artist One", artists.get(0).getName());
        assertEquals("Artist Two", artists.get(1).getName());
        assertEquals("50.0 - 100.0 EUR", event.getPriceInfo());
    }

    @Test
    void parseEventsFromBody_shouldIgnoreNonMusicEvents() throws Exception {
        String json = """
            {
              "_embedded": {
                "events": [
                  {
                    "name": "Sport Event",
                    "dates": {
                      "start": {
                        "localDate": "2025-06-21",
                        "localTime": "15:00:00"
                      }
                    },
                    "classifications": [
                      {
                        "segment": {
                          "name": "Sports"
                        }
                      }
                    ],
                    "_embedded": {
                      "venues": [
                        {
                          "name": "Arena",
                          "city": {"name": "Barcelona"},
                          "country": {"name": "Spain"}
                        }
                      ],
                      "attractions": [
                        {"name": "Team One"}
                      ]
                    }
                  }
                ]
              }
            }
            """;

        List<Event> events = parser.parseEventsFromBody(json);
        assertNotNull(events);
        assertTrue(events.isEmpty());
    }

    @Test
    void parseEventsFromBody_shouldHandleMissingPriceInfo() throws Exception {
        String json = """
            {
              "_embedded": {
                "events": [
                  {
                    "name": "Concert Without Price",
                    "dates": {
                      "start": {
                        "localDate": "2025-07-01",
                        "localTime": "18:30:00"
                      }
                    },
                    "classifications": [
                      {
                        "segment": {
                          "name": "Music"
                        }
                      }
                    ],
                    "_embedded": {
                      "venues": [
                        {
                          "name": "Theater",
                          "city": {"name": "Valencia"},
                          "country": {"name": "Spain"}
                        }
                      ],
                      "attractions": [
                        {"name": "Solo Artist"}
                      ]
                    }
                  }
                ]
              }
            }
            """;

        List<Event> events = parser.parseEventsFromBody(json);
        assertNotNull(events);
        assertEquals(1, events.size());
        Event event = events.getFirst();
        assertEquals("No disponible", event.getPriceInfo());
    }
}
