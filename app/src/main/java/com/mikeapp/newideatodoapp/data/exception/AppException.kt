package com.mikeapp.newideatodoapp.data.exception


open class AppException(open val logMessage: String) : IllegalStateException()

class BackendAppException(override val logMessage: String) : AppException(logMessage)

class RoomDbException(override val logMessage: String) : AppException(logMessage)

class CodeLogicException(override val logMessage: String) : AppException(logMessage)