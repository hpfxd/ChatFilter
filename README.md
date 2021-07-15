# Nate's Chat Filter Plugin
A Regex chat filter plugin

## Features
- **Fast**  
  Checks most messages in under a millisecond.
- **Multiple filter actions**
  - **Block**  
    Blocks the message entirely and sends a message to the user.
    ![Block Demo](https://i.imgur.com/ueBP6AV.png)
  - **Fake**  
    Sends the chat message, but only to the sender.
    ![Fake Demo](https://i.imgur.com/q8RlOyQ.png)
  - **Censor**  
    Replaces the bad words with asterisks.
    ![Censor Demo](https://i.imgur.com/5rS1Q31.png)
- **Highlighted staff notifications**  
  Easily look at why the message was filtered.
- **Customizable**  
  All messages are customizable!
  
## Using
- Go to the [Releases Page](https://github.com/hpfxd/ChatFilter/releases) and download the latest release.
- Drop it in your plugins folder and restart your server.
- Modify the configuration to your liking.
- Optional: Use [the pre-made config](https://gist.github.com/hpfxd/45609d36a6ed71a68a1962cd12d3baf8)

### Permissions
- **chatfilter.bypass**  
  Allows users to bypass the chat filter if `enable-bypass-permission` is enabled in the config.
- **chatfilter.notifications**
  - **chatfilter.notifications.block**  
    Allows users to see notifications when a message is blocked.
  - **chatfilter.notifications.fake**  
    Allows users to see notifications when a message is faked.
  - **chatfilter.notifications.censor**  
    Allows users to see notifications when a message is censored.
    
### Developer API
The only API that exists at this time is the `AsyncChatFilterEvent`.  
You may use this event to cancel when a message gets filtered.
