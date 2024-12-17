# jobs-dict-api

FIXME

## Getting Started

1. Start the application: `lein run`
2. Go to [localhost:8080](http://localhost:8080/) to see: `Hello World!`
3. Read your app's source code at src/jobs_dict_api/service.clj. Explore the docs of functions
   that define routes and responses.
4. Run your app's tests with `lein test`. Read the tests at test/jobs_dict_api/service_test.clj.
5. Learn more! See the [Links section below](#links).


## Configuration

To configure logging see config/logback.xml. By default, the app logs to stdout and logs/.
To learn more about configuring Logback, read its [documentation](http://logback.qos.ch/documentation.html).


## Developing your service

1. Start a new REPL: `lein repl`
2. Start your service in dev-mode: `(def dev-serv (run-dev))`
3. Connect your editor to the running REPL session.
   Re-evaluated code will be seen immediately in the service.

### [Docker](https://www.docker.com/) container support

1. Configure your service to accept incoming connections (edit service.clj and add  ::http/host "0.0.0.0" )
2. Build an uberjar of your service: `lein uberjar`
3. Build a Docker image: `sudo docker build -t jobs-dict-api .`
4. Run your Docker image: `docker run -p 8080:8080 jobs-dict-api`

### [OSv](http://osv.io/) unikernel support with [Capstan](http://osv.io/capstan/)

1. Build and run your image: `capstan run -f "8080:8080"`

Once the image it built, it's cached.  To delete the image and build a new one:

1. `capstan rmi jobs-dict-api; capstan build`


## Links
* [Other Pedestal examples](http://pedestal.io/samples)

# How to test with dev keys and settings.
curl -i -H "Accept: application/json" -H "Content-Type: application/json" \
-A "JobsSpecialUserAgent" \
--header "x-api-key: VBPIIxCie9VjWpMTs5JF8vg3WJlXROcEj/DS4yjOnNjzEy845O9glH7GSSJmaW0NUfjTWZYtTgIxOikh7KxBVXQjgMyETbXVkmFHGsOz5oE=" \
--header "x-api-secret: xjVuDYqL/kVyHMBrkZLMLDvEbvY6bSgf09QZWk4z/hirdC2W1559feHepwwe4LLNiCmzB/QunzlnQAtzXQsHbwNBJHKpI323DbPB5iTMxVo=" \
--header "s-nonce: LzgH9sZy3zTshDjwsXF4LyHNlRjbf4EGzfHvAhT6g9nLHexRLEOEZKpzH53kZFQzo0LZCu+FVVfTNtWBPBV4NK5hBSUXoMVY14nFKPMMnwcTcsTx2TAxMmMoW9ZmnevU" \
http://localhost:8080/wtypes

curl -i -H "Accept: application/json" -H "Content-Type: application/json" \
-A "JobsSpecialUserAgent" \
--header "x-api-key: VBPIIxCie9VjWpMTs5JF8vg3WJlXROcEj/DS4yjOnNjzEy845O9glH7GSSJmaW0NUfjTWZYtTgIxOikh7KxBVXQjgMyETbXVkmFHGsOz5oE=" \
--header "x-api-secret: xjVuDYqL/kVyHMBrkZLMLDvEbvY6bSgf09QZWk4z/hirdC2W1559feHepwwe4LLNiCmzB/QunzlnQAtzXQsHbwNBJHKpI323DbPB5iTMxVo=" \
--header "s-nonce: LzgH9sZy3zTshDjwsXF4LyHNlRjbf4EGzfHvAhT6g9nLHexRLEOEZKpzH53kZFQzo0LZCu+FVVfTNtWBPBV4NK5hBSUXoMVY14nFKPMMnwcTcsTx2TAxMmMoW9ZmnevU" \
http://localhost:8080/etypes

Example:
lein run

Creating your server...
INFO  org.eclipse.jetty.server.Server  - jetty-11.0.20; built: 2024-01-29T21:04:22.394Z; git: 922f8dc188f7011e60d0361de585fd4ac4d63064; jvm 21.0.5+11
INFO  o.e.j.server.handler.ContextHandler  - Started o.e.j.s.ServletContextHandler@5c58cf11{/,null,AVAILABLE}
INFO  o.e.jetty.server.AbstractConnector  - Started ServerConnector@5c8c1f0d{HTTP/1.1, (http/1.1, h2c)}{localhost:8080}
INFO  org.eclipse.jetty.server.Server  - Started Server@76a89ffa{STARTING}[11.0.20,sto=0] @7886ms
#### Run Test Request.
curl -i -H "Accept: application/json" -H "Content-Type: application/json" \
-A "JobsSpecialUserAgent" \
--header "x-api-key: VBPIIxCie9VjWpMTs5JF8vg3WJlXROcEj/DS4yjOnNjzEy845O9glH7GSSJmaW0NUfjTWZYtTgIxOikh7KxBVXQjgMyETbXVkmFHGsOz5oE=" \
--header "x-api-secret: xjVuDYqL/kVyHMBrkZLMLDvEbvY6bSgf09QZWk4z/hirdC2W1559feHepwwe4LLNiCmzB/QunzlnQAtzXQsHbwNBJHKpI323DbPB5iTMxVo=" \
--header "s-nonce: LzgH9sZy3zTshDjwsXF4LyHNlRjbf4EGzfHvAhT6g9nLHexRLEOEZKpzH53kZFQzo0LZCu+FVVfTNtWBPBV4NK5hBSUXoMVY14nFKPMMnwcTcsTx2TAxMmMoW9ZmnevU" \
http://localhost:8080/wtypes
HTTP/1.1 200 OK
Date: Tue, 17 Dec 2024 13:32:15 GMT
X-Frame-Options: DENY
X-XSS-Protection: 1; mode=block
X-Download-Options: noopen
Strict-Transport-Security: max-age=31536000; includeSubdomains
X-Permitted-Cross-Domain-Policies: none
X-Content-Type-Options: nosniff
Content-Security-Policy: object-src 'none'; script-src 'unsafe-inline' 'unsafe-eval' 'strict-dynamic' https: http:;
Content-Type: application/json;charset=utf-8
x-api-key: 4f975047-d276-4751-8d9f-95639a66812e
Content-Length: 557

"[{\"WorkTypeId\":1,\"WorkTypeName\":\"Office\",\"Created\":\"2024-10-09T15:11:46.970678Z\",\"Modified\":\"2024-10-09T15:11:46.970677Z\"},{\"WorkTypeId\":2,\"WorkTypeName\":\"Remote\",\"Created\":\"2024-10-09T15:11:46.970678Z\",\"Modified\":\"2024-10-09T15:11:46.970678Z\"},{\"WorkTypeId\":3,\"WorkTypeName\":\"Office\\\/Remote\",\"Created\":\"2024-10-09T15:11:46.970678Z\",\"Modified\":\"2024-10-09T15:11:46.970678Z\"},{\"WorkTypeId\":4,\"WorkTypeName\":\"Hybrid\",\"Created\
# Dev Mode.
Creating your server...
INFO  org.eclipse.jetty.server.Server  - jetty-11.0.20; built: 2024-01-29T21:04:22.394Z; git: 922f8dc188f7011e60d0361de585fd4ac4d63064; jvm 21.0.5+11
INFO  o.e.j.server.handler.ContextHandler  - Started o.e.j.s.ServletContextHandler@798c002f{/,null,AVAILABLE}
INFO  o.e.jetty.server.AbstractConnector  - Started ServerConnector@550f9818{HTTP/1.1, (http/1.1, h2c)}{localhost:8080}
INFO  org.eclipse.jetty.server.Server  - Started Server@2ae4a15a{STARTING}[11.0.20,sto=0] @7789ms
INFO  io.pedestal.http  - {:msg "GET /wtypes", :line 83}
"uagent-JobsSpecialUserAgent"
"akey-16de814afaf3a815a8b6a9e99410c5c8"
"secret-9478b869b379427b48d5e76eeca02dcc"
"nonce-000058156845796836-638697548651850300-1277395097335139900"
true
{:akey "16de814afaf3a815a8b6a9e99410c5c8", :valid true}
true
true

curl -i -H "Accept: application/json" -H "Content-Type: application/json" \
-A "JobsSpecialUserAgent" \
--header "x-api-key: VBPIIxCie9VjWpMTs5JF8vg3WJlXROcEj/DS4yjOnNjzEy845O9glH7GSSJmaW0NUfjTWZYtTgIxOikh7KxBVXQjgMyETbXVkmFHGsOz5oE=" \
--header "x-api-secret: xjVuDYqL/kVyHMBrkZLMLDvEbvY6bSgf09QZWk4z/hirdC2W1559feHepwwe4LLNiCmzB/QunzlnQAtzXQsHbwNBJHKpI323DbPB5iTMxVo=" \
--header "s-nonce: LzgH9sZy3zTshDjwsXF4LyHNlRjbf4EGzfHvAhT6g9nLHexRLEOEZKpzH53kZFQzo0LZCu+FVVfTNtWBPBV4NK5hBSUXoMVY14nFKPMMnwcTcsTx2TAxMmMoW9ZmnevU" \
http://localhost:8080/etypes
HTTP/1.1 200 OK
Date: Tue, 17 Dec 2024 13:36:20 GMT
X-Frame-Options: DENY
X-XSS-Protection: 1; mode=block
X-Download-Options: noopen
Strict-Transport-Security: max-age=31536000; includeSubdomains
X-Permitted-Cross-Domain-Policies: none
X-Content-Type-Options: nosniff
Content-Security-Policy: object-src 'none'; script-src 'unsafe-inline' 'unsafe-eval' 'strict-dynamic' https: http:;
Content-Type: application/json;charset=utf-8
x-api-key: d111a04d-d1c7-4fbf-9960-7233c6fa9174
Content-Length: 757

"[{\"EmploymentTypeId\":1,\"EmploymentTypeName\":\"full-time\",\"Created\":\"2024-10-09T15:11:46.970680Z\",\"Modified\":\"2024-10-09T15:11:46.970680Z\"},{\"EmploymentTypeId\":2,\"EmploymentTypeName\":\"part-time\",\"Created\":\"2024-10-09T15:11:46.970680Z\",\"Modified\":\"2024-10-09T15:11:46.970680Z\"},{\"EmploymentTypeId\":3,\"EmploymentTypeName\":\"temporary\",\"Created\":\"2024-10-09T15:11:46.970681Z\",\"Modified\":\"2024-10-09T15:11:46.970680Z\"},{\"EmploymentTypeId\":4,\"EmploymentTypeName\":\"contract\",\"Created\":\"2024-10-09T15:11:46.970681Z\",\"Modified\":\"2024-10-09T15:11:46.970681Z\"},{\"EmploymentTypeId\":5,\"EmploymentTypeName\":\"freelance\",\"Created\":\"2024-10-09T15:11:46.970681Z\",\"Modified\":\"2024-10-09T15:11:46.970681Z\"}]"
