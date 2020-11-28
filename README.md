# hEssentialsSRV
An addon for clans that uses JDA to sync discord and game.

Example of sending a signed message to the configured log_channel

```JAVA
List<String> Author = new ArrayList<>();
Author.add("Name");
Author.add("https://www.google.com/search?q=name+url&rlz=1C1CHBF_enUS906US906&oq=name+url&aqs=chrome..69i57j0l6j69i60.12227j1j7&sourceid=chrome&ie=UTF-8");
Author.add("https://lh3.googleusercontent.com/proxy/VLB8qZigtYTriTQhFd6goeWIBuDwKGuU1GC-nVWH4GZw8qbj2uwbaE1a9JPif0bm0lelgd6OhJIMoo0iZl-WNZICHRZ8TwV9jA8z");
HempfestEmbedded simple = DiscordMessage.integration.simple("This title works", "description of yeet", "https://i.imgur.com/6QRQQmz.png", Author.toArray(new String[0]));
simple.setChannelID(HempfestSRV.getPlugin().getChannelID3());
simple.build();
```
