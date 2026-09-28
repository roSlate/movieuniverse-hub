package com.roslate.backend.playlist;

import java.util.List;

public record PlaylistDetail(long id, String name, List<Long> tmdbIds) {
}
