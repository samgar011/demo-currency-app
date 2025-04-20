# Demo Currency Exchange Spring Boot App
---

## Technologies Used
- Java 17
- Spring Boot
- Redis (cache)
- PostgreSQL (database)
- Docker & Docker Compose
- Maven
---

##  Requirements
- Java 17
- Maven
- Docker

---

## 🛠️ Installation & Run with Docker

1. **Clone the repository**
```
git clone https://github.com/samgar011/demo-currency-app.git
```
```
cd demo-currency-exchange
```

2. **Build the project**
```
mvn clean install
```

3. **Run Docker Compose**
```
docker-compose build --no-cache
```
```
docker-compose up
```

This will start the application please do not forget to check your logs on your terminal

---

##  API Documentation
After application start, go to this swagger url beloved
### Swagger URL:
```
http://localhost:8080/swagger-ui/index.html
```

###  POST api/exchange-rate
Exchange rate api.

#### Request Body:
```json
{
  "sourceCurrency": "EUR",
  "targetCurrency": "JPY",
  "useExternalApi": true
}
```

#### Response:
```json
{
  "sourceCurrency": "EUR",
  "targetCurrency": "JPY",
  "rate": 161.682021
}
```

---

###  Post api/conversion
exchange rate with amount

#### Example:
#### Request Body:
```json
{
  "amount": 100,
  "sourceCurrency": "EUR",
  "targetCurrency": "TRY",
  "useExternalApi": true
}
```

#### Response:
```json
{
  "transactionId": "be446ce9-9e67-4990-88a7-08688e2d5346",
  "sourceCurrency": "EUR",
  "targetCurrency": "TRY",
  "sourceAmount": 100,
  "convertedAmount": 4323.8621,
  "exchangeRate": 43.238621
}
```

---

###  Post api/conversion/filter
Filter the exchange rate conversion with transaction id or with date

#### Example:
#### Request Body:
```json
{
  "date": "2025-04-20",
  "page": 0,
  "size": 5
}
```
or
```json
{
  "transactionId": "be446ce9-9e67-4990-88a7-08688e2d5346",
  "page": 0,
  "size": 5
}
```

#### Response:
```json
{
  "conversions": [
    {
      "transactionId": "be446ce9-9e67-4990-88a7-08688e2d5346",
      "sourceCurrency": "EUR",
      "targetCurrency": "TRY",
      "sourceAmount": 100,
      "convertedAmount": 4323.86,
      "exchangeRate": 43.24
    }
  ],
  "totalElements": 1,
  "totalPages": 1,
  "currentPage": 0
}
```
###  Post api/conversion/bulk
use .csv formatted file to use this api 
##  how to create?
- copy items beloved
```json

Amount,SourceCurrency,TargetCurrency
100,EUR,TRY
200,EUR,GBP
100,EUR,JPY

```
- and then open a text file and paste it
- save as .csv format and exit
-  go to swagger "currency-conversion-controller"
- click "/api/conversion/bulk" and try it out 
-  choose file section find your .csv formatted file and Execute

#### Response:
```json
[
  {
    "transactionId": "fea92696-77ba-40a9-a001-3c6bbb724a72",
    "sourceCurrency": "EUR",
    "targetCurrency": "TRY",
    "sourceAmount": 100,
    "convertedAmount": 4323.8618,
    "exchangeRate": 43.238618,
    "errorMessage": null
  },
  {
    "transactionId": "b64eaeb5-e53a-4183-bd38-539e5f4c19b1",
    "sourceCurrency": "EUR",
    "targetCurrency": "GBP",
    "sourceAmount": 200,
    "convertedAmount": 171.4576,
    "exchangeRate": 0.857288,
    "errorMessage": null
  },
  {
    "transactionId": "a93fbb56-10ec-4b1a-982a-49c91f63b569",
    "sourceCurrency": "EUR",
    "targetCurrency": "JPY",
    "sourceAmount": 100,
    "convertedAmount": 16192.4768,
    "exchangeRate": 161.924768,
    "errorMessage": null
  }
]
```
```json
curl -X POST http://localhost:8080/exchange-rate/bulk \
-F "file=@bulk.csv;type=text/csv" \
-F "useExternalService=false"
```


## 📚 Notes
- if you set ``` "useExternalApi" ``` as false you should use base money currency EUR otherwise you will get this error code ``` "code=105, type=base_currency_access_restricted" ``` which means you need to upgrade your plan out of free access plan
- Redis is used to cache exchange rates for faster access.
- The cache TTL (time to live) is set to 10 minutes.
- Setting ``` "useExternalApi" ``` true uses ```  "currencylayer.com" ``` and false ``` "fixer.io" ``` 




