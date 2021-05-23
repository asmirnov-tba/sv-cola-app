package sv.cola.app.controllers;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import sv.cola.app.domain.db.AnswerAttempt;
import sv.cola.app.domain.db.GameProperties;
import sv.cola.app.domain.db.Leader;
import sv.cola.app.domain.db.Play;
import sv.cola.app.domain.db.PlayPoint;
import sv.cola.app.domain.db.PlayPointWithStatus;
import sv.cola.app.domain.db.PromoCode;
import sv.cola.app.domain.db.Question;
import sv.cola.app.domain.db.Spot;
import sv.cola.app.domain.exchange.Answer;
import sv.cola.app.domain.exchange.CodeSubmissionResult;
import sv.cola.app.domain.exchange.CodesSubmissionsAttempt;
import sv.cola.app.domain.exchange.PlayerStatus;
import sv.cola.app.domain.exchange.PlayerStatus.GameStatus;
import sv.cola.app.jpa.AnswerAttemptRepository;
import sv.cola.app.jpa.GamePropertiesRepository;
import sv.cola.app.jpa.LeaderRepository;
import sv.cola.app.jpa.PlayPointRepository;
import sv.cola.app.jpa.PlayPointWithStatusRepository;
import sv.cola.app.jpa.PlayRepository;
import sv.cola.app.jpa.PromoCodeRepository;
import sv.cola.app.jpa.QuestionRepository;
import sv.cola.app.jpa.SpotRepository;

@CrossOrigin(origins = "*", maxAge = 3600)
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
	@Autowired
	private AnswerAttemptRepository answerAttemptRepository;
	@Autowired
	private GamePropertiesRepository gamePropertiesRepository;
	@Autowired
	private PlayPointWithStatusRepository playPointWithStatusRepository;
	@Autowired
	private PromoCodeRepository promoCodeRepository;
	
	@GetMapping("/registration")
	public Play registration(@RequestParam String teamName, @RequestParam String deviceId) throws ResponseStatusException {
		
		GameProperties gp = getGameProperties();
		
		if(System.currentTimeMillis() < gp.getGameStartTs()) {
			throw new ResponseStatusException(HttpStatus.TOO_EARLY);
		} else if (System.currentTimeMillis() > gp.getGameEndTs()) {
			throw new ResponseStatusException(HttpStatus.REQUEST_TIMEOUT);
		}
		
		Play play = new Play();
		
		
		play.setDeviceId(deviceId);
		
		Example<Play> example = Example.of(play);
		
		Optional<Play> optionalPlayFromDB = playRepository.findOne(example);
		
		if (optionalPlayFromDB.isPresent()) {
			
			Play playFromDB = optionalPlayFromDB.get();
			
			if(!playFromDB.getTeamName().equals(teamName)) {
				throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Your device already registered for another team");
			} else {
				return playFromDB;
			}
		
		}
		
		
		play.setDeviceId(null);
		
		play.setTeamName(teamName);
		
		example = Example.of(play);
		
		optionalPlayFromDB = playRepository.findOne(example);
		
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

	private PlayPointWithStatus getCurrentPlayPointByDeviceId(String deviceID) throws ResponseStatusException{
		Play p = getPlayByDeviceId(deviceID);
		
		PlayPointWithStatus pp = new PlayPointWithStatus();
		
		pp.setPlayPtr(p.getId());
		pp.setStatus(0);
		
		List<PlayPointWithStatus> playPointsWithStatus = playPointWithStatusRepository.findAll(Example.of(pp));
		
		if(playPointsWithStatus.size() == 0) {
			return null;
		}
		
		PlayPointWithStatus currentPlayPoint = playPointsWithStatus
				.stream()
				.min(Comparator.comparing(PlayPointWithStatus::getNum))
				.get();
		
		return currentPlayPoint;
		
	}

	private static final Example<GameProperties> EMPTY_GAME_PROPERTIES_EXAMPLE = Example.of(new GameProperties());  
	private GameProperties getGameProperties() throws ResponseStatusException{
		Optional<GameProperties> ogp = gamePropertiesRepository.findOne(EMPTY_GAME_PROPERTIES_EXAMPLE);
		
		if(!ogp.isPresent()) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Game properties not set");
		}
		
		return ogp.get();
	}
	
	@GetMapping("/answer")
	public Answer answer(@RequestParam String deviceId, @RequestParam String answer) throws ResponseStatusException {
		PlayPointWithStatus playPoint = getCurrentPlayPointByDeviceId(deviceId);
		if (playPoint == null) {
			return null;
		}
		
		GameProperties gp = getGameProperties();
		
		if(System.currentTimeMillis() < gp.getGameStartTs()) {
			throw new ResponseStatusException(HttpStatus.TOO_EARLY);
		} else if (System.currentTimeMillis() > gp.getGameEndTs()) {
			throw new ResponseStatusException(HttpStatus.REQUEST_TIMEOUT);
		}
		
		if((playPoint.getAttempt() > 0) && (playPoint.getAnswerTs() + gp.getPenaltyDuration() > System.currentTimeMillis())){
			return Answer.PENALTY_ANSWER;
		}
		
		AnswerAttempt answerAttempt = new AnswerAttempt();
		answerAttempt.setAnswer(answer);
		answerAttempt.setPlayPointPtr(playPoint.getId().intValue());
		answerAttempt.setAnswerTs(System.currentTimeMillis());
		
		answerAttemptRepository.save(answerAttempt);
		
		Question correctAnswer = questionRepository.getOne(playPoint.getQuestionPtr());
		
		if(answer.equals(correctAnswer.getCorrectAnswer())) {
			return Answer.CORRECT_ANSWER;
		} else {
			return Answer.INCORRECT_ANSWER;
		}
	}
	
	@GetMapping("/status")
	public PlayerStatus status(@RequestParam String deviceId) throws ResponseStatusException {
			
			PlayerStatus result = new PlayerStatus();
			
			GameProperties gp = getGameProperties();
			
			
			if(System.currentTimeMillis() < gp.getGameStartTs()) {
				result.setPlayerStatus(GameStatus.GameNotStarted);
				return result;
			} 
		
			Play p = getPlayByDeviceId(deviceId);
			
			result.setRegistrationTS(p.getRegistrationTS());
			
			
			
			result.setPlayerStatus(GameStatus.GameInProgress);
		
			List<Leader> leaders = leaderRepository.findAll();
			if(leaders.size() > 0) {
				Leader leader = leaders.get(0);
				if(leader.getDistanceToFinish() == 0 || System.currentTimeMillis() > gp.getGameEndTs()) {
					if(leader.getPlayPtr() == p.getId()) {
						result.setPlayerStatus(GameStatus.Winner);
					} else {
						result.setPlayerStatus(GameStatus.Loser);
					}
				}
			}
			
			PlayPointWithStatus pp = new PlayPointWithStatus();
			
		
			pp.setPlayPtr(p.getId());
			List<PlayPointWithStatus> playPoints = playPointWithStatusRepository.findAll(Example.of(pp));
			
			if(playPoints.size() == 0) {
				
				return null; // need throw error
			}
			
			result.setTotalPoints(playPoints.size());
			
			PlayPointWithStatus currentPlayPoint = playPoints
					.stream()
					.filter(playPoint -> playPoint.getStatus() == 0)
					.min(Comparator.comparing(PlayPointWithStatus::getNum))
					.orElse(null);
			
			int pointsPassed = Long.valueOf(playPoints
					.stream()
					.filter(playPoint -> playPoint.getStatus() == 1)
					.count()).intValue();
			
			result.setPointsPassed(pointsPassed);
			
			if(currentPlayPoint != null){
				
				if((currentPlayPoint.getAttempt() > 0) && (currentPlayPoint.getAnswerTs() + gp.getPenaltyDuration() > System.currentTimeMillis())){
					result.setPlayerStatus(GameStatus.Penalty);
				}
				
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
				
				result.setCurrentPointId(currentPlayPoint.getPointPtr().intValue());
			}

			return result;
			
	}
	
	@PostMapping("check_codes")
	public ResponseEntity<CodeSubmissionResult> checkCodes(@RequestBody CodesSubmissionsAttempt codesSubmissionsAttempt) throws ResponseStatusException {
		
		PromoCode promoCodeTemplate = new PromoCode();
		if(codesSubmissionsAttempt == null || codesSubmissionsAttempt.getBoatName() == null) {
			
			System.out.println("REGISTRATION\t"+codesSubmissionsAttempt.getBoatName()+"\tFAIL\tWRONG BOAT NAME");
			return ResponseEntity.badRequest().body(CodeSubmissionResult.WRONG_BOAT_NAME);
		}
		promoCodeTemplate.setBoatName(codesSubmissionsAttempt.getBoatName().toUpperCase().trim());
		if(promoCodeRepository.count(Example.of(promoCodeTemplate)) < 1) {
			System.out.println("REGISTRATION\t"+codesSubmissionsAttempt.getBoatName()+"\tFAIL\tWRONG BOAT NAME");
			return ResponseEntity.badRequest().body(CodeSubmissionResult.WRONG_BOAT_NAME);
		};
		
		int i = 0;
		Set<String> processedCodes = new HashSet<>();
		String[] codes;
		if((codes = codesSubmissionsAttempt.getCodes()) !=null) {
			for(String code: codes) {
				if(!promoCodeRepository.existsById(code)) {
					System.out.println("REGISTRATION\t"+codesSubmissionsAttempt.getBoatName()+"\tFAIL\tWRONG CODE\t"+Arrays.toString(codes));
					return ResponseEntity.badRequest().body(CodeSubmissionResult.WRONG_CODE);
				}
				if(processedCodes.contains(code)) {
					System.out.println("REGISTRATION\t"+codesSubmissionsAttempt.getBoatName()+"\tFAIL\tWRONG DUPLICATE CODE\t"+Arrays.toString(codes));
					return ResponseEntity.badRequest().body(CodeSubmissionResult.DUPLICATE_CODE);
				}
				
				processedCodes.add(code);
				i++;
			}
		}
		
		if (getGameProperties().getPromoCodesNeeded() > i) {
			System.out.println("REGISTRATION\t"+codesSubmissionsAttempt.getBoatName()+"\tFAIL\tNOT ENOUGH CODES\t"+Arrays.toString(codes));
			return ResponseEntity.badRequest().body(CodeSubmissionResult.NOT_ENOUGH_CODES);
		}
		System.out.println("REGISTRATION\t"+codesSubmissionsAttempt.getBoatName()+"\tSUCCESS\tSUCCESS\t"+Arrays.toString(codes));
		return ResponseEntity.ok(CodeSubmissionResult.OK);
	}
		
}
