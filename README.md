# Minimal TODO backend

DB config file datasource.properties:
```
driverClassName = com.mysql.cj.jdbc.Driver
jdbcUrl = jdbc:mysql://localhost:3306/minimaltodo
username = root
password = 5f4324bf
dataSource.cachePrepStmts = true
dataSource.prepStmtCacheSize = 250
dataSource.prepStmtCacheSqlLimit = 2048
```

Properties config file base.conf:
```
todo {
    registration {
        confirm {
            lifetime = 1 #days
            url = "http://127.0.0.1:8090/user/registration/confirm/"
        }
    }
    mail {
        auth {
            username = "info.vsco.peoples@gmail.com"
            password = "Z5VjSJN8GcW9fTvE"
        }
        smtp {
            host = "smtp.gmail.com"
            port = 587
            starttls = true
        }
    }
}
```

Endpoints:
#### POST /user/login

request:
```json
{
  "username": "andreu",
  "password": "123456"
}
```

response:
```json
{
  "accessToken": "token",
  "refreshToken": "refresh",
  "refreshTokenExpiresAt": 1656006801089
}
```

#### GET /user/refresh/{refreshToken}

response:
```json
{
  "accessToken": "token",
  "refreshToken": "refresh",
  "refreshTokenExpiresAt": 1656006801089
}
```

#### POST /user/registration

request:
```json
{
  "username": "andreu",
  "email": "test@test.com",
  "password": "123456"
}
```

response: 200 OK or error

#### GET /user/registration/confirm/{confirmToken}
response:
```json
{
  "accessToken": "token",
  "refreshToken": "refresh",
  "refreshTokenExpiresAt": 1656006801089
}
```
#### GET /catalog
Authorization: Bearer {accessToken}

query params:
* own: Boolean?

response:
```json
[
  {
    "id": 1,
    "own": true,
    "ownerId": 12345678,
    "title": "some todo title",
    "items": [
      "do cook",
      "do clean"
    ]
  }
]
```

#### POST /catalog/add
Authorization: Bearer {accessToken}

request:
```json
{
  "title": "title"
}
```

response:
```json
{
  "id": 1
}
```

#### GET /catalog/{id}/share
Authorization: Bearer {accessToken}

response:
```json
{
  "shareSecret": "secretWord"
}
```

#### GET /catalog/shareSecret/{value}
Authorization: Bearer {accessToken}

response:
```json
{
  "id": 1,
  "own": false,
  "ownerId": 12345678,
  "title": "some todo title",
  "items": [
    "do cook",
    "do clean"
  ]
}
```