package ar.com.tandilweb.ApiServer.transport;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.com.tandilweb.ApiServer.dataTypesObjects.generic.ValidationException;
import ar.com.tandilweb.ApiServer.dataTypesObjects.lobby.ChallengeResponse;
import ar.com.tandilweb.ApiServer.dataTypesObjects.lobby.RoomResponse;
import ar.com.tandilweb.ApiServer.persistence.repository.ChallengesRepository;
import ar.com.tandilweb.ApiServer.persistence.repository.RoomsRepository;
import ar.com.tandilweb.ApiServer.persistence.repository.UsersInRoomsRepository;
import ar.com.tandilweb.persistence.domain.Challenges;
import ar.com.tandilweb.persistence.domain.Rooms;

@Service
public class LobbyAdapter {
	
	@Autowired
	RoomsRepository roomsRepository;
	
	@Autowired
	UsersInRoomsRepository uirRepo;
	
	@Autowired
	ChallengesRepository challengesRepository;
	
	public List<RoomResponse> getRooms() {
		List<RoomResponse> out = new ArrayList<RoomResponse>();
		List<Rooms> rooms = roomsRepository.getConnecteds();
		if(rooms != null) {
			for(Rooms room: rooms) {
				RoomResponse item = new RoomResponse();
				item.description = room.getDescription();
				item.gproto = room.getGproto();
				item.id_room = room.getId_room();
				item.isOfficial = room.isOfficial();
				item.max_players = room.getMax_players();
				item.minCoinForAccess = room.getMinCoinForAccess();
				item.name = room.getName();
				item.server_ip = room.getServer_ip();
				item.players = uirRepo.count(room.getId_room());
				out.add(item);
			}
		}
		return out;
	}
	
	public List<RoomResponse> getRoomsByProtocol(String gproto) {
		List<RoomResponse> out = new ArrayList<RoomResponse>();
		List<Rooms> rooms = roomsRepository.getByGproto(gproto);
		for(Rooms room: rooms) {
			RoomResponse item = new RoomResponse();
			item.description = room.getDescription();
			item.gproto = room.getGproto();
			item.id_room = room.getId_room();
			item.isOfficial = room.isOfficial();
			item.max_players = room.getMax_players();
			item.minCoinForAccess = room.getMinCoinForAccess();
			item.name = room.getName();
			item.server_ip = room.getServer_ip();
			out.add(item);
		}
		return out;
	}
	
	public RoomResponse getRoomByID(long roomID) {
		RoomResponse out = new RoomResponse();
		Rooms room = roomsRepository.findById(roomID);
		out.description = room.getDescription();
		out.gproto = room.getGproto();
		out.id_room = room.getId_room();
		out.isOfficial = room.isOfficial();
		out.max_players = room.getMax_players();
		out.minCoinForAccess = room.getMinCoinForAccess();
		out.name = room.getName();
		out.server_ip = room.getServer_ip();
		return out;
	}
	
	public ChallengeResponse challengeRoomByID(long meID, long roomID, String claimToken, Optional<Long> deposit) throws ValidationException  {
		ChallengeResponse out = new ChallengeResponse();
		
		Rooms room = roomsRepository.findById(roomID);
		if (room == null) {
			throw new ValidationException(3, "The room doesn't exists");
		}
		
		Challenges challenge = new Challenges();
		challenge.setChallenge(claimToken);
		challenge.setId_room(roomID);
		challenge.setId_user(meID);
		challenge.setDeposit(deposit.isPresent() ? deposit.get() : null);
		challenge = challengesRepository.create(challenge);
		
		out.challengeID = challenge.getChallengeID();
		out.operationSuccess = true;
		return out;
	}

}
