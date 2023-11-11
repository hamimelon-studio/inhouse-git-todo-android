package com.hongwei.demo.todo.data.datasource.github

object Endpoint {
    const val GITHUB_API_BASE_URL = "https://api.github.com/"

    private const val GITHUB_USER_NAME = "hongwei-bai"

    private const val OPEN_API_REPOSITORY = "open-api-fuelapp"

    const val FUEL_APP_MAIN_ENDPOINT =
        "/repos/$GITHUB_USER_NAME/$OPEN_API_REPOSITORY/contents/accounts/main.json"

    const val FUEL_CARD_IMAGE_TEMPLATE_ENDPOINT =
        "/repos/$GITHUB_USER_NAME/$OPEN_API_REPOSITORY/contents/accounts/fuel_g{id}.png"

    const val FUEL_CARD_ID_STRING_FORMAT_TEMPLATE = "%02d"
}