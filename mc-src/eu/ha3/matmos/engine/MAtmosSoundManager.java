package eu.ha3.matmos.engine;

/*
* ----------------------------------------------------------------------------
* "THE COLA-WARE LICENSE" (Revision 0):
* Hurricaaane wrote this file. As long as you retain this notice you
* can do whatever you want with this stuff. If we meet some day, and you think
* this stuff is worth it, you can buy me a cola in return
* Georges "Hurricaaane" Yam
* ----------------------------------------------------------------------------
*/

public interface MAtmosSoundManager
{
	void routine();
	
	void cacheSound(String path);
	void playSound(String path, float volume, float pitch, int meta);
	
	int getNewStreamingToken();
	
	boolean setupStreamingToken(int token, String path, float volume, float pitch);
	
	void startStreaming(int token, float fadeDuration, int timesToPlay);
	//void restartStreaming(int token, float fadeDuration, int timesToPlay);
	void stopStreaming(int token, float fadeDuration);
	void pauseStreaming(int token, float fadeDuration);
	
	void eraseStreamingToken(int token);
	
}
