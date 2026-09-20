import { describe, expect, it } from 'vitest'
import { normalizePageMetadata, parseJsonResponse } from './client'

describe('API response handling', () => {
  it('preserves 19-digit entity identifiers as strings', () => {
    const parsed = parseJsonResponse(
      '{"code":0,"data":{"courseId":2095367178839474178,"teacherId":900000000000002}}',
    ) as { data: { courseId: string; teacherId: number } }

    expect(parsed.data.courseId).toBe('2095367178839474178')
    expect(parsed.data.teacherId).toBe(900000000000002)
  })

  it('repairs inconsistent zero totals without hiding returned records', () => {
    const page = normalizePageMetadata({
      records: [{ id: '1' }, { id: '2' }],
      current: 1,
      size: 10,
      total: 0,
      pages: 0,
    })

    expect(page.total).toBe(2)
    expect(page.pages).toBe(1)
  })

  it('keeps a next-page hint when a count-less page is full', () => {
    const page = normalizePageMetadata({
      records: [{ id: '1' }, { id: '2' }],
      current: 2,
      size: 2,
      total: 0,
      pages: 0,
    })

    expect(page.total).toBe(5)
    expect(page.pages).toBe(3)
  })
})
