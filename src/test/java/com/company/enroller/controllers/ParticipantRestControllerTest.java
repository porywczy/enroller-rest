package com.company.enroller.controllers;

import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.URI;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.company.enroller.model.Participant;
import com.company.enroller.persistence.MeetingService;
import com.company.enroller.persistence.ParticipantService;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@WebMvcTest(ParticipantRestController.class)
public class ParticipantRestControllerTest {

	@Autowired
	private MockMvc mvc;

	// odcinamy sie od danych; udajemy baze danych
	// if tu Autowired > test integracyjny
	@MockBean
	private MeetingService meetingService;

	@MockBean
	private ParticipantService participantService;

	// symulujemy baze danych
	@Test
	public void getParticipants() throws Exception {
		Participant participant = new Participant();
		participant.setLogin("testlogin");
		participant.setPassword("testpassword");

		// kolekcja z 1 usera
		Collection<Participant> allParticipants = singletonList(participant);
		// like w Mockito
		// if call getAll() zwraca kolekcje
		given(participantService.getAll()).willReturn(allParticipants);

		// JSON-owa kolekcja z serwisu
		mvc.perform(get("/participants").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1))).andExpect(jsonPath("$[0].login", is(participant.getLogin())));
	}

	@Test
	public void getParticipant() throws Exception {
		Participant participant = new Participant();
		participant.setLogin("testlogin");
		participant.setPassword("testpassword");

		// kolekcja z 1 usera
		Collection<Participant> allParticipants = singletonList(participant);
		// like w Mockito
		// if call getAll() zwraca kolekcje
		given(participantService.findByLogin(participant.getLogin())).willReturn(participant);

		// JSON-owa kolekcja z serwisu
		mvc.perform(get("/participants/testlogin").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				// .andExpect(content().string("TUTAJ WPISZ OCZEKIWANEGO JSONa"));
				.andExpect(content().string(new ObjectMapper().writeValueAsString(participant)));
		// .andExpect(jsonPath("logina", is("testlogin")));
	}

	@Test
	public void addParticipant() throws Exception {

		// tworzymy i ustawiamy logowanie dla uczestnika
		Participant participant = new Participant();
		participant.setLogin("testlogin");
		participant.setPassword("testpassword");

		// scenariusz: znajdujemy uczestnika
		given(participantService.findByLogin(participant.getLogin())).willReturn(null);

		// tworzymy JSON z obiektu
		String participantJSON = new ObjectMapper().writeValueAsString(participant);

		// wykonaj POST-a z JSONem
		// oczekujemy:
		// status created
		// content JSON z dodanym uczestnikiem
		mvc.perform(post("/participants").content(participantJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated()).andExpect(content().string(participantJSON));
		//jak assert w JUnit
		verify(participantService, times(1)).add(participant);
	}
	

	@Test
	public void addParticipantThatExists() throws Exception {

		// tworzymy i ustawiamy logowanie dla uczestnika
		Participant participant = new Participant();
		participant.setLogin("testlogin");
		participant.setPassword("testpassword");

		// scenariusz: znajdujemy uczestnika
		given(participantService.findByLogin("testlogin")).willReturn(participant);

		// tworzymy JSON z obiektu
		String participantJSON = new ObjectMapper().writeValueAsString(participant);

		// wykonaj POST-a z JSONem
		// oczekujemy:
		// status created
		// content JSON z dodanym uczestnikiem
		mvc.perform(post("/participants").content(participantJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(content().string("Unable to create. A participant with login \" + participant.getLogin() + \" already exist." ));
				verify(participantService, never()).add(participant);
	}

	@Test
	public void deleteParticipant() throws Exception {
		Participant participant = new Participant();
		participant.setLogin("testlogin");
		participant.setPassword("testpassword");

//		given(participantService.findByLogin(participant.getLogin())).willReturn(participant);
//
//		String participantJSON = new ObjectMapper().writeValueAsString(participant);

		mvc.perform(delete("/participants/testlogin").contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk()).andExpect(content().string(participantJSON));
	}

	@Test
	public void update() throws Exception {
		Participant participant = new Participant();
		participant.setLogin("testlogin");
		participant.setPassword("testpassword");

		// kolekcja z 1 usera
		Collection<Participant> allParticipants = singletonList(participant);
		// like w Mockito
		// if call getAll() zwraca kolekcje
		given(participantService.findByLogin(participant.getLogin())).willReturn(participant);

		// JSON-owa kolekcja z serwisu
		mvc.perform(put("/participants/testlogin").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(content().string("TUTAJ WPISZ OCZEKIWANEGO JSONa"));
	}

}
