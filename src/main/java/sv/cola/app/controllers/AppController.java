package sv.cola.app.controllers;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import sv.cola.app.domain.Answer;
import sv.cola.app.domain.Leader;
import sv.cola.app.domain.Play;
import sv.cola.app.domain.PlayPoint;
import sv.cola.app.domain.PlayerStatus;
import sv.cola.app.domain.PlayerStatus.GameStatus;
import sv.cola.app.domain.Question;
import sv.cola.app.domain.Response;
import sv.cola.app.domain.Spot;
import sv.cola.app.jpa.AnswerRepository;
import sv.cola.app.jpa.LeaderRepository;
import sv.cola.app.jpa.PlayPointRepository;
import sv.cola.app.jpa.PlayRepository;
import sv.cola.app.jpa.QuestionRepository;
import sv.cola.app.jpa.SpotRepository;

@RestController
@RequestMapping("/api/v1")
public class AppController {
	
	
	private static final int SPOTS_NUMBER = 3;
	
	@Autowired
	private PlayRepository playRepository;
	@Autowired
	private PlayPointRepository playPointRepository;
	@Autowired
	private QuestionRepository questionRepository;
	@Autowired
	private SpotRepository spotRepository;
	@Autowired
	private AnswerRepository answerRepository;
	@Autowired
	private LeaderRepository leaderRepository;
	
	@GetMapping("/registration")
	public Play registration(@RequestParam String teamName, @RequestParam String deviceId) throws ResponseStatusException {
		
		Play play = new Play();
		
		play.setTeamName(teamName);
		
		Example<Play> example = Example.of(play);
		
		Optional<Play> optionalPlayFromDB = playRepository.findOne(example);
		
		if(!optionalPlayFromDB.isPresent()) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Team not allowed: " + teamName);
		}
		
		Play playFromDB = optionalPlayFromDB.get();
		
		if(playFromDB.getDeviceId() != null) {
			if(!playFromDB.getDeviceId().equals(deviceId)) {
				throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Another device registered for your team");
			} else {
				return playFromDB;
			}
		}
		
		playFromDB.setDeviceId(deviceId);
		playFromDB.setRegistrationTS(System.currentTimeMillis());
		play = playRepository.save(playFromDB);
		
		List<Question> questions = questionRepository.findAll();
		List<Spot> spots = spotRepository.findAll();
		
		System.out.println(questions.size());
		
		Collections.shuffle(questions);
		Collections.shuffle(spots);
		
		for(int i = 0; i<SPOTS_NUMBER; i++) {
			PlayPoint pp = new PlayPoint();
			pp.setNum(i);
			pp.setPlayPtr(play.getId());
			pp.setQuestionPtr(questions.get(i).getId());
			pp.setPointPtr(spots.get(i).getId());
			pp.setStatus(0);
			playPointRepository.save(pp);
		}
		
		return play;
	}
	
	
	
	private Play getPlayByDeviceId(String deviceID) throws ResponseStatusException{
		Play p = new Play();
		p.setDeviceId(deviceID);
		
		Optional<Play> optionalPlay = playRepository.findOne(Example.of(p));
		if(!optionalPlay.isPresent()) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Your device is not registered");
		}
		
		return optionalPlay.get();
	}

	private PlayPoint getCurrentPlayPointByDeviceId(String deviceID) throws ResponseStatusException{
		Play p = getPlayByDeviceId(deviceID);
		
		PlayPoint pp = new PlayPoint();
		
		pp.setPlayPtr(p.getId());
		pp.setStatus(0);
		
		List<PlayPoint> playPoints = playPointRepository.findAll(Example.of(pp));
		
		if(playPoints.size() == 0) {
			return null;
		}
		
		PlayPoint currentPlayPoint = playPoints
				.stream()
				.min(Comparator.comparing(PlayPoint::getNum))
				.get();
		
		return currentPlayPoint;
		
	}
	
//	@GetMapping("/current_point")
//	public Spot currentPoint(@RequestParam String deviceId) throws ResponseStatusException {
//		PlayPoint playPoint = getCurrentPlayPointByDeviceId(deviceId);
//		if (playPoint == null) {
//			return null;
//		}
//		return spotRepository.getOne(playPoint.getPointPtr());
//	}
//	
//	@GetMapping("/current_question")
//	public Question currentQuestion(@RequestParam String deviceId) throws ResponseStatusException {
//		PlayPoint playPoint = getCurrentPlayPointByDeviceId(deviceId);
//		if (playPoint == null) {
//			return null;
//		}
//		return questionRepository.getOne(playPoint.getQuestionPtr());
//	}
	
	@GetMapping("/answer")
	public Response answer(@RequestParam String deviceId, @RequestParam String answer) throws ResponseStatusException {
		PlayPoint playPoint = getCurrentPlayPointByDeviceId(deviceId);
		if (playPoint == null) {
			return null;
		}
		
		Answer correctAnswer = answerRepository.getOne(playPoint.getQuestionPtr());
		
		if(answer.equals(correctAnswer.getCorrectAnswer())) {
			playPoint.setStatus(1);
			playPoint.setAnswerTS(System.currentTimeMillis());
			playPointRepository.save(playPoint);
			return Response.CORRECT_ANSWER;
		} else {
			return Response.INCORRECT_ANSWER;
		}
	}
	
	@GetMapping("/status")
	public PlayerStatus status(@RequestParam String deviceId) throws ResponseStatusException {
			
			Play p = getPlayByDeviceId(deviceId);
		
			PlayerStatus.GameStatus gameStatus = GameStatus.GameInProgress;
			List<Leader> leaders = leaderRepository.findAll();
			if(leaders.size() > 0) {
				Leader leader = leaders.get(0);
				if(leader.getDistanceToFinish() == 0) {
					if(leader.getPlayPtr() == p.getId()) {
						gameStatus = GameStatus.Winner;
					} else {
						gameStatus = GameStatus.Loser;
					}
				}
			}
			
			PlayPoint pp = new PlayPoint();
			
			pp.setPlayPtr(p.getId());
			
			List<PlayPoint> playPoints = playPointRepository.findAll(Example.of(pp));
			
			if(playPoints.size() == 0) {
				return null; // need throw error
			}
			
			PlayPoint currentPlayPoint = playPoints
					.stream()
					.filter(playPoint -> playPoint.getStatus() == 0)
					.min(Comparator.comparing(PlayPoint::getNum))
					.orElse(null);
			
			int pointsPassed = Long.valueOf(playPoints
					.stream()
					.filter(playPoint -> playPoint.getStatus() == 1)
					.count()).intValue();
			
			Spot curentPoint = null;
			Question currentQuestion = null;
			
			if(currentPlayPoint != null){
				curentPoint = spotRepository.getOne(currentPlayPoint.getPointPtr());
				currentQuestion = questionRepository.getOne(currentPlayPoint.getQuestionPtr());
			}
			
			return new PlayerStatus(gameStatus, playPoints.size(), pointsPassed, curentPoint, currentQuestion, p.getRegistrationTS());
	}
		
}
