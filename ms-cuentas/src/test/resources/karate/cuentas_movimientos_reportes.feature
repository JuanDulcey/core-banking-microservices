Feature: Casos de Uso 2, 3, 4, F3 (Saldo no disponible) y Caso de Uso 5 (Estado de Cuenta)

  Background:
    * def port = karate.properties['karate.port']
    * url 'http://localhost:' + port

  Scenario: Flujo completo bancario de Cuentas, Movimientos, Validación de Saldo y Reportes
    # Caso de Uso 2: Creación de Cuentas de Usuario
    Given path '/api/cuentas'
    And request { "numeroCuenta": "478758", "tipoCuenta": "Ahorros", "saldoInicial": 2000, "estado": true, "cliente": "Jose Lema" }
    When method post
    Then status 201
    And match response.saldoDisponible == 2000.0

    Given path '/api/cuentas'
    And request { "numeroCuenta": "225487", "tipoCuenta": "Corriente", "saldoInicial": 100, "estado": true, "cliente": "Marianela Montalvo" }
    When method post
    Then status 201

    Given path '/cuentas'
    And request { "numeroCuenta": "495878", "tipoCuenta": "Ahorros", "saldoInicial": 0, "estado": true, "cliente": "Juan Osorio" }
    When method post
    Then status 201

    Given path '/api/cuentas'
    And request { "numeroCuenta": "496825", "tipoCuenta": "Ahorros", "saldoInicial": 540, "estado": true, "cliente": "Marianela Montalvo" }
    When method post
    Then status 201

    # Caso de Uso 3: Crear una nueva Cuenta Corriente para Jose Lema
    Given path '/api/cuentas'
    And request { "numeroCuenta": "585545", "tipoCuenta": "Corriente", "saldoInicial": 1000, "estado": true, "cliente": "Jose Lema" }
    When method post
    Then status 201
    And match response.tipoCuenta == 'Corriente'

    # Caso de Uso 4: Realizar movimientos contables
    # 4.1 Retiro de 575 en 478758 -> Saldo resultante 1425
    Given path '/api/movimientos'
    And request { "numeroCuenta": "478758", "tipoMovimiento": "Retiro", "valor": -575, "fecha": "10/2/2022" }
    When method post
    Then status 201
    And match response.saldo == 1425.0

    # 4.2 Depósito de 600 en 225487 -> Saldo resultante 700
    Given path '/api/movimientos'
    And request { "numeroCuenta": "225487", "tipoMovimiento": "Deposito", "valor": 600, "fecha": "10/2/2022" }
    When method post
    Then status 201
    And match response.saldo == 700.0

    # 4.3 Depósito de 150 en 495878 -> Saldo resultante 150
    Given path '/movimientos'
    And request { "numeroCuenta": "495878", "valor": 150, "fecha": "9/2/2022" }
    When method post
    Then status 201
    And match response.saldo == 150.0

    # 4.4 Retiro de 540 en 496825 -> Saldo resultante 0
    Given path '/api/movimientos'
    And request { "numeroCuenta": "496825", "tipoMovimiento": "Retiro", "valor": -540, "fecha": "8/2/2022" }
    When method post
    Then status 201
    And match response.saldo == 0.0

    # F3: Validación de Saldo no disponible (intentando retirar de 496825 que quedó en 0)
    Given path '/api/movimientos'
    And request { "numeroCuenta": "496825", "tipoMovimiento": "Retiro", "valor": -50 }
    When method post
    Then status 422
    And match response.detail == 'Saldo no disponible'
    And match response.message == 'Saldo no disponible'

    # Caso de Uso 5 (F4): Reporte de Estado de Cuenta para Marianela Montalvo
    Given path '/api/reportes'
    And param fecha = '8/2/2022,10/2/2022'
    And param cliente = 'Marianela Montalvo'
    When method get
    Then status 200
    And match response.cliente == 'Marianela Montalvo'
    And match response.cuentas == '#[2]'
    And match response.movimientos == '#[2]'
    And match response.movimientos[0]['Fecha'] == '10/2/2022'
    And match response.movimientos[0]['Numero Cuenta'] == '225487'
    And match response.movimientos[0]['Movimiento'] == 600.0
    And match response.movimientos[0]['Saldo Disponible'] == 700.0
    And match response.movimientos[1]['Fecha'] == '8/2/2022'
    And match response.movimientos[1]['Numero Cuenta'] == '496825'
    And match response.movimientos[1]['Movimiento'] == -540.0
    And match response.movimientos[1]['Saldo Disponible'] == 0.0
