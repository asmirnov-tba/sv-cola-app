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

import sv.cola.app.domain.db.Leader;
import sv.cola.app.domain.db.Play;
import sv.cola.app.domain.db.PlayPoint;
import sv.cola.app.domain.db.Question;
import sv.cola.app.domain.db.Spot;
import sv.cola.app.domain.response.PlayerStatus;
import sv.cola.app.domain.response.PlayerStatus.GameStatus;
import sv.cola.app.domain.response.Answer;
import sv.cola.app.jpa.LeaderRepository;
import sv.cola.app.jpa.PlayPointRepository;
import sv.cola.app.jpa.PlayRepository;
import sv.cola.app.jpa.QuestionRepository;
import sv.cola.app.jpa.SpotRepository;

@RestController
@RequestMapping("/api/v1")
public class AppController {
	
	@Autowired
	private PlayRepository playRepository;
	@Autowired
	private PlayPointRepository playPointRepository;
	@Autowired
	private QuestionRepository questionRepository;
	@Autowired
	private SpotRepository spotRepository;
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
		
		for(int i = 0; i<play.getPointsPlanned(); i++) {
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

	@GetMapping("/answer")
	public Answer answer(@RequestParam String deviceId, @RequestParam String answer) throws ResponseStatusException {
		PlayPoint playPoint = getCurrentPlayPointByDeviceId(deviceId);
		if (playPoint == null) {
			return null;
		}
		
		Question correctAnswer = questionRepository.getOne(playPoint.getQuestionPtr());
		
		if(answer.equals(correctAnswer.getCorrectAnswer())) {
			playPoint.setStatus(1);
			playPoint.setAnswerTS(System.currentTimeMillis());
			playPointRepository.save(playPoint);
			return Answer.CORRECT_ANSWER;
		} else {
			return Answer.INCORRECT_ANSWER;
		}
	}
	
	@GetMapping("/status")
	public PlayerStatus status(@RequestParam String deviceId) throws ResponseStatusException {
			
			PlayerStatus result = new PlayerStatus();
		
			Play p = getPlayByDeviceId(deviceId);
			
			result.setRegistrationTS(p.getRegistrationTS());
			
			result.setPlayerStatus(GameStatus.GameInProgress);
		
			List<Leader> leaders = leaderRepository.findAll();
			if(leaders.size() > 0) {
				Leader leader = leaders.get(0);
				if(leader.getDistanceToFinish() == 0) {
					if(leader.getPlayPtr() == p.getId()) {
						result.setPlayerStatus(GameStatus.Winner);
					} else {
						result.setPlayerStatus(GameStatus.Loser);
					}
				}
			}
			
			PlayPoint pp = new PlayPoint();
			
			pp.setPlayPtr(p.getId());
			List<PlayPoint> playPoints = playPointRepository.findAll(Example.of(pp));
			
			if(playPoints.size() == 0) {
				return null; // need throw error
			}
			
			result.setTotalPoints(playPoints.size());
			
			PlayPoint currentPlayPoint = playPoints
					.stream()
					.filter(playPoint -> playPoint.getStatus() == 0)
					.min(Comparator.comparing(PlayPoint::getNum))
					.orElse(null);
			
			int pointsPassed = Long.valueOf(playPoints
					.stream()
					.filter(playPoint -> playPoint.getStatus() == 1)
					.count()).intValue();
			
			result.setPointsPassed(pointsPassed);
			
			if(currentPlayPoint != null){
				Spot currentPoint = spotRepository.getOne(currentPlayPoint.getPointPtr());
				
				result.setCurrentPointLat(currentPoint.getLat());
				result.setCurrentPointLon(currentPoint.getLon());
				result.setCurrentPointHint(currentPoint.getHint());
				result.setCurrentPointRenderId(currentPoint.getRenderId());
				
				Question currentQuestion = questionRepository.getOne(currentPlayPoint.getQuestionPtr());
				
				result.setCurrentQuestionTxt(currentQuestion.getTxt());
				result.setCurrentQuestionOptionA(currentQuestion.getOptionA());
				result.setCurrentQuestionOptionB(currentQuestion.getOptionB());
				result.setCurrentQuestionOptionC(currentQuestion.getOptionC());
				result.setCurrentQuestionOptionD(currentQuestion.getOptionD());
			}

			return result;
			
	}
		
}
