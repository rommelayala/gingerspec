@ignore @toocomplex
@rest
Feature: Datatable replacements

  Scenario: Data table replacement enviroment(passed in variable)
    Given I open a ssh connection to '${SSH}' with user 'privalia' and password 'temporal'
    And I send requests to 'jenkins-ci.privalia.pin:80'
    And I send a 'POST' request to '/FNF' based on 'schemas/rest.json' as 'json' with:
      | $.type | UPDATE | ${SLEEPTEST} |
    Then the service response status must be '404'

  Scenario: Data table replacement enviroment(save in scenario)
    Given I open a ssh connection to '${SSH}' with user 'privalia' and password 'temporal'
    And I send requests to 'jenkins-ci.privalia.pin:80'
    When I run 'echo datatable' in the ssh connection and save the value in environment variable 'ELEM'
    And I send a 'POST' request to '/FNF' based on 'schemas/rest.json' as 'json' with:
      | $.type | UPDATE | !{ELEM} |
    Then the service response status must be '404'
