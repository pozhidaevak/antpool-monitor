# antpool-monitor
It monitors hashrate of antpool workers and sends messages to notify if any worker has stoped.
Currently it uses Telegram Bot API to send messages. 
I want to realize the next logic:
1. For each worker you can create a rule with hash rate threshold
2. When hashrate is lower than the threshold a message is sent
3. If there is no worker with specified name a message is sent
4. When everything becomes ok another message is sent

Work in progress...
Code is very messy :(
