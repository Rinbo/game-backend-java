package nu.borjessons.web.game_backend.service.impl;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nu.borjessons.web.game_backend.models.Highscore;
import nu.borjessons.web.game_backend.models.HighscoreRepository;
import nu.borjessons.web.game_backend.service.HighscoreService;

@Service
public class HighscoreServiceImpl implements HighscoreService {

  @Autowired
  HighscoreRepository highscoreRepository;

  @Override
  public Highscore getEntryAndPositionOfName(String name) {
    ArrayList<Highscore> scoreList = highscoreRepository.findAllByOrderByScoreDesc();
    Highscore namedHighscore = new Highscore();

    Integer count = 1;
    for (Highscore highscore : scoreList) {
      if (highscore.getName().equals(name)) {
        highscore.setFlashRank(count);
        highscoreRepository.save(highscore);
        namedHighscore = highscore;
        break;
      }
      count += 1;
    }
    return namedHighscore;
  }

}