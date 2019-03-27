package dae.rounder.events

import dae.rounder.database.entity.Game

data class OnGameClickedEvent(val game: Game)