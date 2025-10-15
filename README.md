PSP System (Scala) - Multi-module

Modules
- psp-domain: domain models
- psp-core: validation, routing, payment orchestration
- psp-storage: in-memory repository
- psp-acquirer-mock: mock acquirers
- psp-api: Tapir/http4s API
- psp-app: wiring and server

Run
```bash
sbt psp-app/run
```

Server: http://localhost:8080

API
- POST /payments
Request:
```json
{
  "cardNumber": "4242424242424242",
  "expiry": "2027-12",
  "cvv": "123",
  "amount": 10.5,
  "currency": "USD",
  "merchantId": "m-1"
}
```

Response:
```json
{"transactionId":"...","status":"Approved|Denied|Pending","message":"Processed"}
```

Swagger UI: http://localhost:8080/docs

Notes
- Luhn validation applied
- BIN routing: sum of first 6 digits, even→A, odd→B. Acquirer decision by last PAN digit parity.
- In-memory storage keeps transaction records for the process lifetime.

Encryption (brief)
- TLS in transit; tokenize PAN; no CVV storage; AES-256-GCM via KMS/HSM if persisted.


