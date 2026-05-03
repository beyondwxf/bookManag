package com.codex.library.common;

import java.util.List;

public record PageResult<T>(long total, long current, long size, List<T> records) {
}
