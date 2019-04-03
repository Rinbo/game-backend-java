package nu.borjessons.web.game_backend.service;

import nu.borjessons.web.game_backend.models.Highscore;

public interface HighscoreService {
  Highscore getEntryAndPositionOfName(String name);
}