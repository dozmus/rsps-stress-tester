rsps-stress-tester
========

A stress tester for runescape private servers.

This intentionally does not come with support for proxies, distribution of work,
or tor, because it is not intended for malicious activity.

It can idle at a spot, and also type chat messages.

You can specify messages to type using `-m hello1,hello2`.
If they contain spaces you can use `-m "hello world","hello world 2"`.

```
Usage: java -jar rsps-stress-tester.jar [options]
  Options:
    --host, -h
      Server hostname
      Default: 127.0.0.1
    --messages, -m
      Messages
      Default: []
    --number, -n
      Number of bot clients
      Default: 1
    --port, -p
      Server port
      Default: 43594
    --threads, -t
      Number of threads to use
      Default: 1
```

This project is licensed under the [MIT License](LICENSE).
