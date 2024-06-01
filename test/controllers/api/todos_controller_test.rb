require "test_helper"

class Api::TodosControllerTest < ActionDispatch::IntegrationTest
  test "should get create" do
    get api_todos_create_url
    assert_response :success
  end
end
