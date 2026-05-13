# asupg-service

## Overview
ASUPG service acts as the main entry point for all user requests. It is a billing application for
managing and monitoring payments from client companies. It provides endpoints for managing:

- **Users** — authentication and authorization
- **Companies** — registered in the ASUPG system
- **Devices** — mostly gas meters, registered to companies
- **Transactions** — managed by `asupg-workers`, which integrates with an external Bank API
- **Reports** — aggregated statistics and dashboards

### Related Services
| Service         | Role                                      | Integration point                                                                                   |
|-----------------|-------------------------------------------|-----------------------------------------------------------------------------------------------------|
| `asupg-core`    | Source of truth for companies and devices | Best-effort device sync — see `org.asupg.asupgservice.service.AsupgCoreService`                     |
| `asupg-workers` | Background jobs, Bank API integration     | Monthly billing and transaction sync — see `org.asupg.asupgservice.api.impl.JobAdminControllerImpl` |
| `asupg-infra`   | Infrastructure and deployment             | Docker, MongoDB setup                                                                               |

`asupg-service` also exposes endpoints for managing the Bank API credentials used by `asupg-workers`.

---

## Architecture

**Database:** MongoDB, configured as a replica set (single master node). The replica set is required
to enable multi-document transactions even though only one node is active.
- Repositories: `org.asupg.asupgservice.repository`
- Custom repositories (cursor-based pagination and search): `org.asupg.asupgservice.repository.custom`
- MongoDB transaction config: `org.asupg.asupgservice.config.MongoConfig`

**Auth:**
- Authentication: JWT tokens via `org.asupg.asupgservice.filter.JwtAuthenticationFilter`
- Authorization: role-based — `USER` (read-only) and `ADMIN` (full access), enforced at the method level

**External clients (Feign):**
- `asupg-workers`: `org.asupg.asupgservice.client.workers`
- `asupg-core`: `org.asupg.asupgservice.client.asupg`
- Job/admin endpoints that call these clients: `org.asupg.asupgservice.api.JobAdminController`

---

## Getting Started

### Prerequisites
1. Copy `asupg-service.env.example` → `asupg-service.env` at the project root and fill in all required variables.
2. Start MongoDB in Docker (see `asupg-infra` repo for the compose file).
3. `asupg-workers` is optional but required for job-related endpoints to function.

### Run locally
```
Run AsupgServiceApplication from the project root once the prerequisites above are ready.
```

### Tests
No unit or integration tests are currently present.

---

## Common Tasks

### Add a new endpoint
1. Check if a related controller interface already exists in `org.asupg.asupgservice.api`.
2. If not, create the interface there (used for API documentation).
3. Create the implementation under `org.asupg.asupgservice.api.impl`.
4. Use **method-level security annotations** for authorization — every existing endpoint follows this pattern; do not deviate.

### Deploy
Deployment is managed in the `asupg-infra` repository. Steps:

1. Check the current version in `application.yml`.
2. Determine the next version using semantic versioning:
    - `+1.x.x` — breaking change
    - `x.+1.x` — non-breaking new feature
    - `x.x.+1` — bug fix
3. Build and push the multi-platform Docker image:
   ```bash
   docker buildx build --platform linux/amd64,linux/arm64 \
     -t shuhikjuriknarik/asupg-service:${version} --push .
   ```
4. Make sure to update the version in `application.yml` after build was successfully finished.
5. Update the version reference in `asupg-infra` and follow the deployment steps there.

---

## Conventions
- **Controllers:** always split into an interface (`org.asupg.asupgservice.api`) and an implementation (`...api.impl`).
- **Authorization:** always use method-level security — never rely solely on URL patterns.
- **External service calls:** use Feign clients only; do not introduce raw HTTP clients.
- **Pagination:** custom repositories in `...repository.custom` handle cursor-based pagination — follow the same pattern when adding new paginated endpoints.
- **Transactions:** any operation requiring atomicity must go through the configured MongoDB transaction manager in `MongoConfig`.

---

## Gotchas
- **MongoDB replica set:** The DB runs as a replica set with a single master. This is intentional — MongoDB requires a replica set to support multi-document transactions. Do not change this to a standalone instance.
- **No test suite:** There are currently no automated tests. Manual testing against a local environment is the only verification step before deploying.
- **Bank API credentials:** These are stored and managed via `asupg-service` endpoints but consumed exclusively by `asupg-workers`. Changes to the credential storage schema require coordinating with the workers service.