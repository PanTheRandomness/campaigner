package com.example.application.services;

import com.example.application.data.*;
import com.example.application.data.repositories.*;
import jakarta.annotation.PostConstruct;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class DataInitializer {

    private final WorldRepository worldRepository;
    private final AreaRepository areaRepository;
    private final PlaceRepository placeRepository;
    private final UserRepository userRepository;
    private final CampaignRepository campaignRepository;
    private final CalendarRepository calendarRepository;
    private final EventRepository eventRepository;
    private final EventTypeRepository eventTypeRepository;
    private final EventDurationRepository eventDurationRepository;
    private final MoonRepository moonRepository;

    public DataInitializer(

            CampaignRepository campaignRepository,
            WorldRepository worldRepository,
            AreaRepository areaRepository,
            PlaceRepository placeRepository,
            UserRepository userRepository,
            CalendarRepository calendarRepository,
            EventRepository eventRepository,
            EventTypeRepository eventTypeRepository,
            EventDurationRepository eventDurationRepository,
            MoonRepository moonRepository) {
        this.campaignRepository = campaignRepository;
        this.worldRepository = worldRepository;
        this.areaRepository = areaRepository;
        this.placeRepository = placeRepository;
        this.userRepository = userRepository;
        this.calendarRepository = calendarRepository;
        this.eventRepository = eventRepository;
        this.eventTypeRepository = eventTypeRepository;
        this.eventDurationRepository = eventDurationRepository;
        this.moonRepository = moonRepository;
    }

    @PostConstruct
    public void initData() {
        try {
            if (campaignRepository.count() == 0) {
                // Create world
                World world = new World();
                world.setWorldName("Kingdom of Kar");
                world.setWorldDescription("Tummien metsien ja pyhien vuorten maa");
                world.setWorldHistory("Muinaisten valtakuntien perintö.");
                worldRepository.save(world);

                // Create areas
                Area area = new Area();
                area.setAreaName("Dungrada");
                area.setAreaDescription("Vanha metsä lännessä");
                area.setWorld(world);
                areaRepository.save(area);

                Area area2 = new Area();
                area2.setAreaName("West Coast");
                area2.setAreaDescription("Kingdom of Kar's Western Coast");
                area2.setWorld(world);
                areaRepository.save(area2);

                // Create places
                Place place = new Place();
                place.setPlaceName("Starglen");
                place.setPlaceDescription("Tähtien katseluun sopiva kylä metsän laidalla.");
                place.setArea(area);
                placeRepository.save(place);

                Place place2 = new Place();
                place2.setPlaceName("Velemouth");
                place2.setPlaceDescription("The biggest trading city & port of the nation");
                place2.setArea(area2);
                placeRepository.save(place2);

                // Create users
                User gm = new User();
                gm.setUsername("gm");
                gm.setName("Game Master");
                gm.setEmail("gm@example.com");
                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                String hash = encoder.encode("admin"); //changing password is recommended
                gm.setHashedPassword(hash);
                gm.setRoles(Set.of(Role.ADMIN));
                userRepository.save(gm);

                User player = new User();
                player.setUsername("pelaaja");
                player.setName("Maija Pelaaja");
                player.setEmail("pelaaja@example.com");
                String hash2 = encoder.encode("test"); //changing password is recommended
                player.setHashedPassword(hash2);
                player.setRoles(Set.of(Role.USER));
                userRepository.save(player);

                // Create Moon
                Moon moon = new Moon();
                moon.setMoonName("Luna");
                moon.setCycle(28);
                moon.setShift(14);
                moonRepository.save(moon);

                // Create calendar
                Calendar calendar = new Calendar();
                calendar.setCalendarName("Karilainen kalenteri");
                calendar.setMonthsInYear(4);
                calendar.setDaysInMonth(30);
                calendar.setDaysInWeek(3);
                calendar.setCurrentDate(new CalendarDate(312,5,1));

                List<String> months = new ArrayList<>();
                months.add("First Month");
                months.add("Second Month");
                months.add("Third Month");
                months.add("Fourth Month");
                calendar.setMonthNames(months);

                List<String> weekdays = new ArrayList<>();
                weekdays.add("Monday");
                weekdays.add("Tuesday");
                weekdays.add("Wednesday");
                weekdays.add("Thursday");
                weekdays.add("Friday");
                weekdays.add("Saturday");
                weekdays.add("Sunday");
                calendar.setWeekdayNames(weekdays);

                calendar.setMoonCount(1);
                List<Moon> moons = new ArrayList<>();
                moons.add(moon);
                calendar.setMoons(moons);
                calendarRepository.save(calendar);

                // Create campaign
                Campaign campaign = new Campaign();
                campaign.setCampaignName("Dungradan seikkailut");
                campaign.setCampaignDescription("Tutkimusmatka metsän sydämeen");
                campaign.setCampaignWorld(world);
                campaign.setCalendar(calendar);
                campaign.setGms(List.of(gm));
                campaign.setPlayers(List.of(player));
                campaignRepository.save(campaign);

                // Create event types
                EventType eventType = new EventType();
                eventType.setEventType("Adventure");
                eventType.setEventColour("#FF0000");
                eventTypeRepository.save(eventType);

                EventType eventType2 = new EventType();
                eventType2.setEventType("Festivals");
                eventType2.setEventColour("#FF00DDDD");
                eventTypeRepository.save(eventType2);

                // Create durations
                EventDuration duration = new EventDuration();
                CalendarDate startDate = new CalendarDate(290,1,1);
                CalendarDate endDate = new CalendarDate(290,2,1);
                duration.setStartDate(startDate);
                duration.setEndDate(endDate);
                duration.setDuration(calendar);
                eventDurationRepository.save(duration);

                EventDuration duration2 = new EventDuration();
                CalendarDate startDate2 = new CalendarDate(0,3,1);
                duration2.setStartDate(startDate2);
                duration2.setDuration(calendar);
                eventDurationRepository.save(duration2);

                // Create events
                Event event = new Event();
                event.setName("Metsän kutsu");
                event.setDescription("Salaperäinen ääni houkuttelee sankarit metsään.");
                event.setCampaign(campaign);
                event.setType(eventType);
                event.setPlace(place);
                event.setDuration(duration);
                event.setReoccurring(ReoccurrenceType.NONE); //Muutettu Set.of()
                eventRepository.save(event);

                Event event2 = new Event();
                event2.setName("Kevätmarkkinat");
                event2.setDescription("Velemouthin suuret kevätmarkkinat");
                event2.setCampaign(campaign);
                event2.setType(eventType2);
                event2.setPlace(place2);
                event2.setDuration(duration2);
                event2.setReoccurring(ReoccurrenceType.YEARLY); //Muutettu Set.of()
                eventRepository.save(event2);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
