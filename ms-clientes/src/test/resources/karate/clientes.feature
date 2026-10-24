Feature: Gestión integral de Clientes (Caso de Uso 1 y validaciones CRUD)

  Background:
    * def port = karate.properties['karate.port']
    * url 'http://localhost:' + port

  Scenario: Crear los 3 clientes del Caso de Uso 1, consultar, actualizar y eliminar
    # 1. Crear Jose Lema por /api/clientes
    Given path '/api/clientes'
    And request
      """
      {
        "nombres": "Jose Lema",
        "direccion": "Otavalo sn y principal",
        "telefono": "098254785",
        "contrasena": "1234",
        "estado": true
      }
      """
    When method post
    Then status 201
    And match response.nombre == 'Jose Lema'
    And match response.estado == true
    And match response.clienteId == '#notnull'
    * def joseId = response.id
    * def joseCodigo = response.clienteId

    # 2. Crear Marianela Montalvo usando el alias directo /clientes
    Given path '/clientes'
    And request
      """
      {
        "nombre": "Marianela Montalvo",
        "direccion": "Amazonas y NNUU",
        "telefono": "097548965",
        "contrasena": "5678",
        "estado": true
      }
      """
    When method post
    Then status 201
    And match response.nombre == 'Marianela Montalvo'

    # 3. Crear Juan Osorio
    Given path '/api/clientes'
    And request
      """
      {
        "nombre": "Juan Osorio",
        "direccion": "13 junio y Equinoccial",
        "telefono": "098874587",
        "contrasena": "1245",
        "estado": true
      }
      """
    When method post
    Then status 201
    * def juanId = response.id

    # 4. Consultar Jose Lema por su código de negocio clienteId
    Given path '/api/clientes', joseCodigo
    When method get
    Then status 200
    And match response.id == joseId
    And match response.direccion == 'Otavalo sn y principal'

    # 5. Actualizar parcialmente (PATCH) teléfono de Jose Lema
    Given path '/api/clientes', joseId
    And request { "telefono": "099111222" }
    When method patch
    Then status 200
    And match response.telefono == '099111222'

    # 6. Eliminar a Juan Osorio y verificar 404 posterior
    Given path '/api/clientes', juanId
    When method delete
    Then status 204

    Given path '/api/clientes', juanId
    When method get
    Then status 404
