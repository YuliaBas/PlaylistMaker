package com.example.playlistmaker.player.domain
import com.example.playlistmaker.player.data.PlayerHandler
import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.config.App.Companion.TAG
import java.util.concurrent.Executors

class PlayerInteractorImpl(
    private val playerHandler: PlayerHandler,
    private val repository: SelectedTrackRepository
) : PlayerInteractor {
    private val executor = Executors.newCachedThreadPool()


    override fun getTrackDetails(
        trackJsonString: String,
        consumer: PlayerInteractor.TrackConsumer
    ) {
        executor.execute {
            try {
                val track = repository.decodeTrackDetails(trackJsonString)
                consumer.consume(track)
            } catch (t: Throwable) {
                consumer.onError(t)
            }
        }
    }

    override fun preparePlayer(track: Track, onPrepared: () -> Unit, onCompletion: () -> Unit) {
        playerHandler.preparePlayer(track, onPrepared, onCompletion)
    }


    override fun startPlayer() {
        playerHandler.play()
    }

    override fun pausePlayer() {
        playerHandler.pause()
    }

    override fun getCurrentPosition(): String {
        return playerHandler.getCurrentPosition()
    }

    override fun onDestroy() {
        playerHandler.shutdownPlayer()
    }

}