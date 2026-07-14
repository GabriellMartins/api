# API Documentation - KodexERP Backend

## Estrutura do Projeto

O projeto está organizado em módulos para facilitar a manutenção e escalabilidade:

```
src/main/kotlin/com/kodexerp/backend/
├── auth/              # Login/Register
│   ├── controller/
│   ├── service/
│   ├── dto/
│   ├── entity/
│   └── repository/
├── subscription/      # Planos/Assinaturas
│   ├── controller/
│   ├── service/
│   ├── dto/
│   ├── entity/
│   └── repository/
├── admin/            # Dashboard/Admin
│   ├── controller/
│   ├── service/
│   ├── dto/
│   ├── entity/
│   └── repository/
└── shared/           # Configurações compartilhadas
    ├── config/
    ├── security/
    ├── exception/
    └── dto/
```

## Autenticação

### Register
```http
POST /auth/register
Content-Type: application/json

{
  "name": "João Silva",
  "email": "joao@example.com",
  "password": "senha123",
  "phone": "11999999999",
  "company": "Empresa Ltda"
}
```

**Response:**
```json
{
  "success": true,
  "data": {
    "user": {
      "id": "uuid",
      "name": "João Silva",
      "email": "joao@example.com",
      "role": "USER"
    },
    "token": "jwt_token",
    "refreshToken": "refresh_token"
  }
}
```

### Login
```http
POST /auth/login
Content-Type: application/json

{
  "email": "joao@example.com",
  "password": "senha123"
}
```

### Logout
```http
POST /auth/logout
Authorization: Bearer {token}
```

### Refresh Token
```http
POST /auth/refresh
Content-Type: application/json

{
  "refreshToken": "refresh_token"
}
```

## Planos Públicos (Landing Page)

### Listar Planos
```http
GET /public/plans
```

**Response:**
```json
{
  "plans": [
    {
      "id": "uuid",
      "name": "Starter",
      "price": 49.00,
      "interval": "MONTHLY",
      "description": "Pra quem tá começando. Tudo que precisa pra começar a emitir notas.",
      "features": [
        "Até 50 notas por mês",
        "Suporte por email",
        "API básica",
        "Relatórios simples",
        "1 usuário"
      ],
      "maxInvoices": 50,
      "maxUsers": 1,
      "apiAccess": true,
      "prioritySupport": false,
      "isPopular": false
    },
    {
      "id": "uuid",
      "name": "Pro",
      "price": 149.00,
      "interval": "MONTHLY",
      "description": "Pra quem tá crescendo. Mais recursos e suporte prioritário.",
      "features": [
        "Até 500 notas por mês",
        "Suporte prioritário",
        "API completa",
        "Relatórios avançados",
        "5 usuários",
        "Webhooks",
        "Integrações"
      ],
      "maxInvoices": 500,
      "maxUsers": 5,
      "apiAccess": true,
      "prioritySupport": true,
      "isPopular": true
    },
    {
      "id": "uuid",
      "name": "Enterprise",
      "price": 399.00,
      "interval": "MONTHLY",
      "description": "Pra grandes negócios. Sem limites e suporte dedicado.",
      "features": [
        "Notas ilimitadas",
        "Suporte dedicado 24/7",
        "API personalizada",
        "Relatórios customizados",
        "Usuários ilimitados",
        "SLA garantido",
        "Gerente de conta",
        "Treinamento da equipe"
      ],
      "maxInvoices": null,
      "maxUsers": null,
      "apiAccess": true,
      "prioritySupport": true,
      "isPopular": false
    }
  ]
}
```

## Assinaturas (Autenticado)

### Listar Planos Disponíveis
```http
GET /subscriptions/plans
Authorization: Bearer {token}
```

### Ver Assinatura Atual
```http
GET /subscriptions/current
Authorization: Bearer {token}
```

**Nota:** Novos usuários recebem automaticamente 14 dias de trial do plano Starter.

### Assinar Plano
```http
POST /subscriptions/subscribe
Authorization: Bearer {token}
Content-Type: application/json

{
  "planId": "uuid",
  "paymentMethod": "credit",
  "interval": "monthly",
  "cardToken": "tok_visa"
}
```

### Cancelar Assinatura
```http
POST /subscriptions/cancel
Authorization: Bearer {token}
Content-Type: application/json

{
  "cancelAtPeriodEnd": true
}
```

### Atualizar Método de Pagamento
```http
POST /subscriptions/update-payment
Authorization: Bearer {token}
Content-Type: application/json

{
  "paymentMethod": "credit",
  "cardToken": "tok_visa"
}
```

## Admin (ROLE_ADMIN)

### Gerenciar Planos

#### Listar Todos os Planos
```http
GET /admin/plans
Authorization: Bearer {admin_token}
```

#### Buscar Plano por ID
```http
GET /admin/plans/{id}
Authorization: Bearer {admin_token}
```

#### Criar Plano
```http
POST /admin/plans
Authorization: Bearer {admin_token}
Content-Type: application/json

{
  "name": "Custom Plan",
  "price": 99.00,
  "interval": "MONTHLY",
  "features": "Feature 1,Feature 2,Feature 3",
  "maxInvoices": 100,
  "maxUsers": 3,
  "apiAccess": true,
  "prioritySupport": false,
  "isActive": true
}
```

#### Editar Plano
```http
PUT /admin/plans/{id}
Authorization: Bearer {admin_token}
Content-Type: application/json

{
  "name": "Updated Plan Name",
  "price": 129.00
}
```

#### Deletar Plano
```http
DELETE /admin/plans/{id}
Authorization: Bearer {admin_token}
```

#### Inicializar Planos Padrão
```http
POST /admin/plans/initialize
Authorization: Bearer {admin_token}
```

**Nota:** Execute este endpoint na primeira vez para criar os 3 planos padrão (Starter, Pro, Enterprise).

### Dashboard

#### Dashboard
```http
GET /admin/dashboard
Authorization: Bearer {admin_token}
```

#### Listar Usuários
```http
GET /admin/users?page=1&limit=20&search=&role=&status=
Authorization: Bearer {admin_token}
```

#### Listar Assinaturas
```http
GET /admin/subscriptions?page=1&limit=20&status=&plan=
Authorization: Bearer {admin_token}
```

#### Receita
```http
GET /admin/revenue?startDate=&endDate=&groupBy=month
Authorization: Bearer {admin_token}
```

#### Configurações
```http
GET /admin/settings
Authorization: Bearer {admin_token}
```

```http
PUT /admin/settings
Authorization: Bearer {admin_token}
Content-Type: application/json

{
  "maintenanceMode": false,
  "registrationEnabled": true,
  "maxUsersPerPlan": {
    "starter": 5,
    "pro": 20,
    "enterprise": 100
  }
}
```

## Trial Automático

Quando um usuário se registra:
1. Recebe automaticamente 14 dias de trial
2. Usa o plano Starter (mais barato)
3. Tem acesso a todas as funcionalidades do plano Starter
4. Após 14 dias, precisa assinar formalmente

## Configuração

### Variáveis de Ambiente
```properties
# JWT
jwt.secret=your-secret-key
jwt.expiration=86400000
jwt.refresh-expiration=604800000

# CORS
app.cors.allowed-origins=http://localhost:3000,http://localhost:5173

# Database (configure no application.properties)
```

## Inicialização

1. **Inicializar planos padrão:**
   ```bash
   POST /admin/plans/initialize
   ```

2. **Criar usuário admin:**
   - Use o endpoint `/auth/register`
   - Altere o role para ADMIN no banco de dados

3. **Testar trial:**
   - Registre um novo usuário
   - Chame `/subscriptions/current`
   - Verifique que retorna status TRIAL com plano Starter

## Erros Comuns

### 401 Unauthorized
- Token inválido ou expirado
- Use `/auth/refresh` para renovar

### 403 Forbidden
- Usuário não tem permissão
- Endpoints admin requerem ROLE_ADMIN

### 400 Bad Request
- Validação falhou
- Verifique os campos obrigatórios

## Próximos Módulos

Estrutura preparada para:
- **payment/** - Processamento de pagamentos
- **invoice/** - Emissão de notas fiscais
- **webhook/** - Webhooks e integrações
